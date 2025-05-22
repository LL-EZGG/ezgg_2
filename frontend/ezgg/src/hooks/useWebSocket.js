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
export const useWebSocket = ({onMessage, onConnect, onDisconnect, onError}) => {
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
            await api.get('/auth/memberinfo');
            return true;
        } catch (error) {
            console.log('[useWebSocket.js] Token validation or refresh failed:', error);
            return false;
        }
    }

    /** STOMP 서버 연결 함수 */
    const connect = useCallback(async (onConnectedCallback) => {
        if (stompClient.current && stompClient.current.connected) {
            // 이미 연결된 경우 바로 콜백 실행
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
            if (onDisconnect) onDisconnect(); // 연결이끊겼을때 호출됨
        };

        stompClient.current.connect({},
            () => {
                console.log("[useWebSocket.js]\nWebSocket connected");
                setIsConnected(true);


                // 개별 유저의 매칭 결과 구독
                stompClient.current.subscribe(`/user/queue/matching`, (message) => {
                    const response = JSON.parse(message.body);
                    onMessage(response);
                });

                // 에러 구독
                stompClient.current.subscribe(`/user/queue/errors`, (message) => {
                    onError(message.body);
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
    }, [onConnect, onMessage, onDisconnect, onError]);

    /** 연결 해제 함수 */
    const disconnect = useCallback(() => {
        if (stompClient.current) {
            stompClient.current.disconnect(() => {
                setIsConnected(false);
                if (onDisconnect) onDisconnect();
            });
        }
    }, [onDisconnect]);

    /**
     * 매칭 요청 전송 함수.
     *  - ViewModel → DTO 변환 후 JSON 직렬화하여 전송
     */
    const sendMatchingRequest = useCallback(async (criteriaVM) => {
        const dtoPayload = criteriaToDTO(criteriaVM);
        const json = JSON.stringify(dtoPayload);

        if (!stompClient.current?.connected) {// 연결이 없으면 먼저 connect 후 전송
            // 연결 전에 토큰 유효성 검증
            const isTokenValid = await validateToken();
            if (!isTokenValid) {
                if (onError) onError('인증이 만료되었습니다');
                return;
            }
            connect(() => { // 연결 보장 후 전송
                stompClient.current.send('/app/matching/start', {}, json);
            });
            return;
        }
        stompClient.current.send('/app/matching/start', {}, json);
    }, [connect]);

    /**
     * 매칭 취소 요청 전송 함수
     * 백엔드에서 Redis에 저장된 매칭 정보를 삭제하도록 요청
     */

    const sendCancelRequest = useCallback(async () => {
        if (!stompClient.current?.connected) {
            // 연결이 없으면 먼저 connect 후 전송
            const isTokenValid = await validateToken();
            if (!isTokenValid) {
                if (onError) onError('인증이 만료되었습니다');
                return;
            }
            connect(() => {
                stompClient.current.send('/app/matching/stop', {}, JSON.stringify({}));
                console.log('[useWebSocket.js] 매칭 취소 요청 전송');
            });
            return;
        }
        stompClient.current.send('/app/matching/stop', {}, JSON.stringify({}));
        console.log('[useWebSocket.js] 매칭 취소 요청 전송');
    }, [connect]);

    return {connect, disconnect, sendMatchingRequest, isConnected, sendCancelRequest};
};
