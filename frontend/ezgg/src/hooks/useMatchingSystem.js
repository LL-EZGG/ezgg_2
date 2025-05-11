import {useEffect, useState} from 'react';
import { isValidCriteria } from '../utils/validation.js';
import {useWebSocket} from "./useWebSocket.js";
import {getInitialCriteria} from "../utils/initialStates.js";

export const useMatchingSystem = () => {
  const [matchResult, setMatchResult] = useState(null);
  const [matchingCriteria, setMatchingCriteria] = useState(getInitialCriteria());
  const [isMatching, setIsMatching] = useState(false);

  const { connect, disconnect, sendMatchingRequest } = useWebSocket({
    onMessage: (response) => {
      alert('매칭 성공! 상대방 정보를 확인해주세요.');
      setMatchResult(response)
      disconnect();
    },
    onDisconnect: () => handleMatchCancel()
    // onMessage: (response) => {
    //   console.log(response)
    //   if (response.status === 'SUCCESS') {
    //     setMatchResult(response.data);
    //     alert('매칭 성공! 상대방 정보를 확인하세요.')
    //     disconnect()
    //     setIsMatching(false);
    //   } else {
    //     console.error('매칭 실패:', response.message);
    //     alert('매칭에 실패하였습니다. 조건을 다시 설정해주세요.')
    //     disconnect();
    //   }
    //   setIsMatching(false);
    // },
    // onDisconnect: () => {
    //   console.log('연결 종료');
    //   setIsMatching(false);
    // },
    // onError: (message) => {
    //   alert('에러 발생 : ' + message);
    //   setIsMatching(false);
    // }
  });

  const handleMatchStart = (criteria) => {
    if (!isValidCriteria(criteria)) {
      alert('라인은 필수로 선택해주세요.');
      return;
    }

    setIsMatching(() => true);
    connect(() => {
      sendMatchingRequest(criteria);
      setMatchingCriteria(criteria);
    });
  };

  const handleMatchCancel = () => {
    disconnect();
    setMatchResult(null);
    setIsMatching(false);
    setMatchingCriteria(getInitialCriteria());
  };

  useEffect(() => {
    return () => disconnect();
  }, []);

  return {
    matchResult,
    setMatchResult,
    matchingCriteria,
    setMatchingCriteria,
    isMatching,
    setIsMatching,
    handleMatchStart,
    handleMatchCancel
  };
};
