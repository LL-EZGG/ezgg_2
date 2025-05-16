import {useCallback, useRef, useState} from 'react';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import api from '../utils/api';

export const useWebSocket = ({onMessage, onConnect, onDisconnect, onError}) => {
    const stompClient = useRef(null);
    const [isConnected, setIsConnected] = useState(false);

    // 토큰 유효성 검증 함수
    const validateToken = async () => {
      try {
        const token = tokenUtils.get();
        if(!token) {
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

    const connect = useCallback(async(onConnectedCallback) => {
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
          console.error("[useWebSocket.js] WebSocket error", error);
          setIsConnected(false);
          if (onDisconnect) onDisconnect();
        }
      );
    }, [onConnect, onMessage, onDisconnect, onError]);

    const disconnect = useCallback(() => {
        if (stompClient.current) {
            stompClient.current.disconnect(() => {
                setIsConnected(false);
                if (onDisconnect) onDisconnect();
            });
        }
    }, [onDisconnect]);

    const sendMatchingRequest = useCallback(async(payload) => {
        // 필요한 데이터만 추출하여 최적화된 페이로드 생성
        const optimizedPayload = {
            wantLine: payload.wantLine,
            selectedChampions: {
                preferredChampions: payload.selectedChampions?.preferredChampions?.map(champ => champ.id) || [],
                bannedChampions: payload.selectedChampions?.bannedChampions?.map(champ => champ.id) || []
            }
        };

        if (!stompClient.current?.connected) {
            // 연결 전에 토큰 유효성 검증
            const isTokenValid = await validateToken();
            if (!isTokenValid) {
              if (onError) onError('인증이 만료되었습니다');
              return;
            }
            connect(() => { // 연결 보장 후 전송
                stompClient.current.send('/app/matching/start', {}, JSON.stringify(optimizedPayload));
            });
            return;
        }
        stompClient.current.send('/app/matching/start', {}, JSON.stringify(optimizedPayload));
    }, [connect]);

    return {connect, disconnect, sendMatchingRequest, isConnected};
};
