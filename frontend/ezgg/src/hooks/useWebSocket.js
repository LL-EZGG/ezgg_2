import { useEffect, useRef, useCallback } from 'react';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

export const useWebSocket = ({ userId, onMessage, onConnect, onDisconnect }) => {
  const stompClient = useRef(null);

  const connect = useCallback(() => {
    const socket = new SockJS('http://localhost:8888/ws');
    stompClient.current = Stomp.over(socket);

    const token = localStorage.getItem('accessToken');
    
    stompClient.current.connect(token ? { Authorization: `Bearer ${token}` } : {}, 
        () => {
            if (onConnect) onConnect();
            
            // 개별 유저의 매칭 결과 구독
            stompClient.current.subscribe(`/topic/matching/${userId}`, (message) => {
                const response = JSON.parse(message.body);
                onMessage(response);
            });
    });
  }, [userId, onConnect, onMessage]);

  const disconnect = useCallback(() => {
    if (stompClient.current) {
      stompClient.current.disconnect();
      if (onDisconnect) onDisconnect();
    }
  }, [onDisconnect]);

  const sendMessage = useCallback((destination, message) => {
    if (stompClient.current && stompClient.current.connected) {
      stompClient.current.send(destination, {}, JSON.stringify(message));
    }
  }, []);

  useEffect(() => {
    connect();
    return () => {
      disconnect();
    };
  }, [connect, disconnect]);

  return { sendMessage };
};
