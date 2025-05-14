import {useCallback, useRef, useState} from 'react';
import SockJS from 'sockjs-client';
import {Stomp} from '@stomp/stompjs';

export const useWebSocket = ({onMessage, onConnect, onDisconnect, onError}) => {
    const stompClient = useRef(null);
    const [isConnected, setIsConnected] = useState(false);

    const connect = useCallback((onConnectedCallback) => {
        if (stompClient.current && stompClient.current.connected) {
            // 이미 연결된 경우 바로 콜백 실행
            onConnectedCallback?.();
            return;
        }

        const token = localStorage.getItem('token');
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
                    onMessage(response);
                });

                stompClient.current.subscribe(`/user/queue/error`, (message) => {
                    onError(message.body);
                });

                if (onConnect) onConnect();
                onConnectedCallback?.();
            },
            (error) => {
                console.error("WebSocket error", error);
                setIsConnected(false);
                if (onDisconnect) onDisconnect();
            }
        );
    }, [onConnect, onMessage, onDisconnect]);

    const disconnect = useCallback(() => {
        if (stompClient.current) {
            stompClient.current.disconnect(() => {
                setIsConnected(false);
                if (onDisconnect) onDisconnect();
            });
        }
    }, [onDisconnect]);

    // const sendMatchingRequest = useCallback((payload) => {
    //   if (!stompClient.current?.connected) {
    //     connect(() => { // 연결 보장 후 전송
    //       stompClient.current.send('/app/matching/start', {}, JSON.stringify(payload));
    //     });
    //     return;
    //   }
    //   stompClient.current.send('/app/matching/start', {}, JSON.stringify(payload));
    // }, [connect]);
    const sendMatchingRequest = useCallback((payload) => {
        // 필요한 데이터만 추출하여 최적화된 페이로드 생성
        const optimizedPayload = {
            wantLine: payload.wantLine,
            selectedChampions: {
                preferredChampions: payload.selectedChampions?.preferredChampions?.map(champ => champ.id) || [],
                bannedChampions: payload.selectedChampions?.bannedChampions?.map(champ => champ.id) || []
            }
        };

        if (!stompClient.current?.connected) {
            connect(() => { // 연결 보장 후 전송
                stompClient.current.send('/app/matching/start', {}, JSON.stringify(optimizedPayload));
            });
            return;
        }
        stompClient.current.send('/app/matching/start', {}, JSON.stringify(optimizedPayload));
    }, [connect]);

    return {connect, disconnect, sendMatchingRequest, isConnected};
};
