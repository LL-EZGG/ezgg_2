// useWebSocket.js
// -----------------------------------------------------------------------------
// STOMP WebSocket 커스텀 훅
// - UI에서 사용하는 ViewModel(criteria)을 DTO로 변환 후 서버로 전송
// - onMessage/onConnect/onDisconnect/onError 콜백 지원
// -----------------------------------------------------------------------------
import {useCallback, useRef, useState} from 'react';
import SockJS from 'sockjs-client';
import {Stomp} from '@stomp/stompjs';
import api, {tokenUtils} from '../utils/api';

// ---------------- ViewModel → DTO 변환기 --------------------------------------
/**
 * 매칭 조건 ViewModel을 DTO로 변환하는 함수.
 *  - 챔피언 객체 → id 문자열 배열로 축소
 *  - 불필요 필드는 제거
 */
const criteriaToDTO = (vm) => ({
    wantLine: vm.wantLine,
    userPreferenceText: vm.userPreferenceText,
    selectedChampions: {
        preferredChampions: vm.selectedChampions?.preferredChampions?.map((c) => c.id) ?? [],
        bannedChampions: vm.selectedChampions?.bannedChampions?.map((c) => c.id) ?? [],
    }
});

// ---------------- 커스텀 훅 -----------------------------------------------------
export const useWebSocket = ({onMessage, onConnect, onDisconnect, onError, onChatMessage}) => {
    const stompClient = useRef(null);
    const [isConnected, setIsConnected] = useState(false);

    // 토큰 유효성 검증 함수
    const validateToken = async () => {
        try {
            const token = tokenUtils.get();
            if (!token) {
                console.log("[useWebSocket.js]\nToken not found");
                return false;
            }
            // 단순히 API 요청을 보내서 토큰 검증
            // 401이 떨어지면 api.js의 인터셉터가 자동으로 토큰을 재발급 받음
            await api.post('/auth/validateToken');
          
            return true;
        } catch (error) {
            console.log('[useWebSocket.js] Token validation or refresh failed:', error);
            return false;
        }
    }

    /** STOMP 서버 연결 함수 */
    const connect = useCallback(async (onConnectedCallback) => {
        if (stompClient.current && stompClient.current.connected) {
            console.log('[useWebSocket.js] 이미 연결되어 있음');
            onConnectedCallback?.();
            return;
        }

        // 웹소켓 연결 전 토큰 유효성 검증
        const isTokenValid = await validateToken();
        if (!isTokenValid) {
            console.error('[useWebSocket.js] Invalid or expired token and refresh failed');
            if (onError) onError('인증이 만료되었습니다. 다시 로그인해주세요.');
            return;
        }

        const token = tokenUtils.get();
        console.log("[useWebSocket.js]\ntoken: ", token);
        const socket = new SockJS(`http://localhost:8888/ws?token=${token}`);
        stompClient.current = Stomp.over(socket);

        socket.onclose = () => {
            setIsConnected(false);
            if (onDisconnect) onDisconnect();
        };

        stompClient.current.connect({},
            () => {
                console.log("[useWebSocket.js]\nWebSocket connected");
                setIsConnected(true);

                // 개별 유저의 매칭 결과 구독
                stompClient.current.subscribe(`/user/queue/matching`, (message) => {
                    const response = JSON.parse(message.body);
                    console.log('[useWebSocket] 매칭 완료 메시지:', response);
                    console.log('[useWebSocket] 매칭 메시지 전체 구조:', JSON.stringify(response, null, 2));

                    // 서버 응답 구조에 맞게 수정
                    if (response.status === "SUCCESS" && response.data?.chattingRoomId) {
                        console.log('[useWebSocket] 채팅방 구독 시작:', response.data.chattingRoomId);

                        // 🔥 브로드캐스트 구독 (확실한 메시지 수신)
                        const topicSub = stompClient.current.subscribe(`/topic/chat/${response.data.chattingRoomId}`, (chatMsg) => {
                            const chatResponse = JSON.parse(chatMsg.body);
                            console.log("[useWebSocket] 브로드캐스트 메시지 수신됨!:", chatResponse);
                            if (onChatMessage) {
                                console.log("[useWebSocket] onChatMessage 호출!");
                                onChatMessage(chatResponse);
                            }
                        });
                        console.log('[useWebSocket] 브로드캐스트 구독 완료:', topicSub.id);

                        // 개별 사용자 큐 구독 (백업)
                        const userQueueSub = stompClient.current.subscribe(`/user/queue/${response.data.chattingRoomId}`, (chatMsg) => {
                            const chatResponse = JSON.parse(chatMsg.body);
                            console.log("[useWebSocket]개별 메시지 수신:", chatResponse);
                            if (onChatMessage) onChatMessage(chatResponse);
                        });
                        console.log('[useWebSocket] 개별 큐 구독 완료:', userQueueSub.id);
                    }

                    if (onMessage) onMessage(response);
                });

                // 에러 구독
                stompClient.current.subscribe(`/user/queue/errors`, (message) => {
                    if (onError) onError(message.body);
                });

                if (onConnect) onConnect();
                onConnectedCallback?.();
            },
            (error) => {
                console.error("[useWebSocket.js] WebSocket error", error);
                setIsConnected(false);
                if (onDisconnect) onDisconnect();
            }
        );
    }, [onConnect, onMessage, onDisconnect, onError, onChatMessage]);

    /** 연결 해제 함수 */
    const disconnect = useCallback(() => {
        if (stompClient.current) {
            stompClient.current.disconnect(() => {
                console.log('[useWebSocket.js] 연결 해제됨');
                setIsConnected(false);
                if (onDisconnect) onDisconnect();
            });
            stompClient.current = null;
        }
    }, [onDisconnect]);

    /**
     * 매칭 요청 전송 함수.
     *  - ViewModel → DTO 변환 후 JSON 직렬화하여 전송
     */
    const sendMatchingRequest = useCallback(async (criteriaVM) => {
        const dtoPayload = criteriaToDTO(criteriaVM);
        const json = JSON.stringify(dtoPayload);

        if (!stompClient.current?.connected) {
            console.log('[useWebSocket.js] 연결되지 않음, 연결 후 매칭 요청 전송');
            const isTokenValid = await validateToken();
            if (!isTokenValid) {
                if (onError) onError('인증이 만료되었습니다');
                return;
            }
            connect(() => {
                if (stompClient.current?.connected) {
                    stompClient.current.send('/app/matching/start', {}, json);
                    console.log('[useWebSocket.js] 매칭 요청 전송됨');
                }
            });
            return;
        }
        stompClient.current.send('/app/matching/start', {}, json);
        console.log('[useWebSocket.js] 매칭 요청 전송됨');
    }, [connect, onError]);

    /**
     * 매칭 취소 요청 전송 함수
     */
    const sendCancelRequest = useCallback(async () => {
        if (!stompClient.current?.connected) {
            console.log('[useWebSocket.js] 연결되지 않음, 연결 후 매칭 취소 요청 전송');
            const isTokenValid = await validateToken();
            if (!isTokenValid) {
                if (onError) onError('인증이 만료되었습니다');
                return;
            }
            connect(() => {
                if (stompClient.current?.connected) {
                    stompClient.current.send('/app/matching/stop', {}, JSON.stringify({}));
                    console.log('[useWebSocket.js] 매칭 취소 요청 전송됨');
                }
            });
            return;
        }
        stompClient.current.send('/app/matching/stop', {}, JSON.stringify({}));
        console.log('[useWebSocket.js] 매칭 취소 요청 전송됨');
    }, [connect, onError]);

    /**
     * 채팅 메시지 전송 함수
     */
    const sendChatMessage = useCallback(async (chattingRoomId, message, sender) => {
        if (!stompClient.current?.connected) {
            console.log('[useWebSocket.js] 연결되지 않음, 채팅 메시지 전송 실패');
            if (onError) onError('웹소켓 연결이 끊어졌습니다.');
            return;
        }

        const chatData = {
            chattingRoomId: chattingRoomId,
            message: message,
            sender: sender,
            timestamp: new Date().toISOString()
        };

        stompClient.current.send('/app/chat/send', {}, JSON.stringify(chatData));
        console.log('[useWebSocket.js] 채팅 메시지 /app/chat/send로 전송됨:', chatData);
    }, [onError]);

    return {
        socket: stompClient.current,
        connect,
        disconnect,
        sendMatchingRequest,
        sendCancelRequest,
        sendChatMessage,
        isConnected
    };
};
