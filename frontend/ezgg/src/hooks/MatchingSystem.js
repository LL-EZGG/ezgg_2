import { useState } from 'react';

export const useMatchingSystem = () => {
  const [isMatching, setIsMatching] = useState(false);
  const [matchResult, setMatchResult] = useState(null);
  const [matchingCriteria, setMatchingCriteria] = useState(null);

  const handleMatchStart = (criteria) => {
    setIsSeMatching(true);
    setMatchingCriteria(criteria);

    // API 호출 및 매칭 로직
  };

  const handleMatchCancel = () => {
    setIsSeMatching(false);
    setMatchResult(null);
  };

  return {
    isMatching,
    matchResult,
    matchingCriteria,
    handleMatchStart,
    handleMatchCancel
  };
};
