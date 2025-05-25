import {useEffect, useState} from 'react';
import {isValidCriteria} from '../utils/validation.js';
import {getInitialCriteria} from "../utils/initialStates.js";

export const useMatchingSystem = ({socket, sendMatchingRequest, sendCancelRequest, onMatchMessage}) => {
    const [matchResult, setMatchResult] = useState(null);
    const [matchingCriteria, setMatchingCriteria] = useState(getInitialCriteria());
    const [isMatching, setIsMatching] = useState(false);

    const handleMatchStart = (criteria) => {
        if (!isValidCriteria(criteria)) {
            alert('라인은 필수로 선택해주세요.');
            return;
        }

        if (!socket) {
            alert('웹소켓 연결이 필요합니다.');
            return;
        }

        console.log('[useMatchingSystem] 매칭 시작:', criteria);
        setIsMatching(true);
        setMatchingCriteria(criteria);

        // App.jsx에서 전달받은 sendMatchingRequest 사용
        sendMatchingRequest(criteria);
    };

    const handleMatchCancel = () => {
        console.log('[useMatchingSystem] 매칭 취소');

        if (isMatching) {
            // 매칭 중일 때만 취소 요청을 백엔드로 전송
            sendCancelRequest();
        }

        // 상태 초기화
        setMatchResult(null);
        setIsMatching(false);
        setMatchingCriteria(getInitialCriteria());
    };

    useEffect(() => {
        // 컴포넌트 언마운트 시 정리
        return () => {
            if (isMatching) {
                // 매칭 중에 컴포넌트가 언마운트되면 취소 요청 전송
                console.log('[useMatchingSystem] 컴포넌트 언마운트, 매칭 취소');
                sendCancelRequest();
            }
        };
    }, [isMatching, sendCancelRequest]);

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
