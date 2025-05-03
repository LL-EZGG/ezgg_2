import {useRef, useCallback, useState} from 'react';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

export const useWebSocket = ({ onMessage, onConnect, onDisconnect }) => {
    const stompClient = useRef(null);
    const [isConnected, setIsConnected] = useState(false);

    const connect = useCallback((onConnectedCallback) => {
    if (stompClient.current && stompClient.current.connected) {
        // 이미 연결된 경우 바로 콜백 실행
        onConnectedCallback?.();
        return;
    }

    const token = localStorage.getItem('accessToken');
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

  const sendMatchingRequest = useCallback((payload) => {
      console.log("[useWebSocket.js]\nsendMatchingRequest", payload);
      console.log("[useWebSocket.js]\nisConnected : ", isConnected);

      if (stompClient.current?.connected) {
          console.log(">>> 연결 후 send 호출")
          stompClient.current.send('/app/matching/start', {}, JSON.stringify(payload));
      } else {
          console.warn("[useWebSocket - sendMatchingRequest] 아직 연결되지 않음");
      }

  }, [isConnected]);

  return { connect, disconnect, sendMatchingRequest, isConnected };
};
