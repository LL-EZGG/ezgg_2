import {useEffect, useState} from 'react';
import {isValidCriteria} from '../utils/validation.js';
import {useWebSocket} from "./useWebSocket.js";
import {getInitialCriteria} from "../utils/initialStates.js";

export const useMatchingSystem = () => {
    const [matchResult, setMatchResult] = useState(null);
    const [matchingCriteria, setMatchingCriteria] = useState(getInitialCriteria());
    const [isMatching, setIsMatching] = useState(false);

    const {connect, disconnect, sendMatchingRequest, sendCancelRequest} = useWebSocket({
        onMessage: (response) => {
            alert('매칭 성공! 상대방 정보를 확인해주세요.');
            setMatchResult(response)
            disconnect();
        }, onConnect: () => {
            console.log('매칭 시스템 웹소켓 연결 완료');
        }, onDisconnect: () => {
            console.log('매칭 시스템 웹소켓 연결 종료');
            setMatchingCriteria(getInitialCriteria());
            setIsMatching(false);
        }, onError: (message) => {
            alert('에러 발생: ' + message);
            setIsMatching(false);
        }
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

    // const handleDisconnect = () => {
    //     disconnect();
    //     setIsMatching(false);
    //     setMatchingCriteria(getInitialCriteria());
    // } -> 이거는 필요없을듯 useWebSocket에서 연결 해제하는 함수가 이미 있음

    const handleMatchCancel = () => {
        if (isMatching) {
            // 매칭 중일 때만 취소 요청을 백엔드로 전송
            sendCancelRequest();
        }
        // 상태 초기화
        setMatchResult(null);
        setIsMatching(false);
        setMatchingCriteria(getInitialCriteria());
        // 웹소켓 연결 종료
        disconnect();
    };

    useEffect(() => {
        // 컴포넌트 언마운트 시 정리
        return () => {
            //매칭 중일때만 요청 (페이지를 떠나거나 하게되면 취소)
            if (isMatching) {
                // 매칭 중에 컴포넌트가 언마운트되면 취소 요청 전송
                sendCancelRequest();
            }
            disconnect();
        };
    }, [isMatching]);

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
