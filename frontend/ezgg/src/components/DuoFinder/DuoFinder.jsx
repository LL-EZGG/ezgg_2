import React from 'react';
import styled from '@emotion/styled';
import {UserProfileCard} from "./user/UserProfileCard.jsx";
import {MatchingInterface} from "./matching/MatchingInterface.jsx";

const DuoFinder = (
  {
    memberDataBundle,
    isLoading,
    userInfo,
    matchingCriteria,
    isMatching,
    setMatchingCriteria,
    matchResult
  }
) => {
    return (
      <Container>
        <UserProfileCard
          userInfo={userInfo}
          memberDataBundle={memberDataBundle}
          isLoading={isLoading}
        />

        {matchResult ? (
          <UserProfileCard
            userInfo={userInfo}
            memberDataBundle={memberDataBundle}
            isLoading={isLoading}
          />
        ) : (
          <MatchingInterface
            matchResult={matchResult}
            matchingCriteria={matchingCriteria}
            isMatching={isMatching}
            setMatchingCriteria={setMatchingCriteria}
          />
        )}
      </Container>
    )
};

export default DuoFinder;

const Container = styled.div`
    display: flex;
    gap: 2rem;
    padding: 2rem;
    width: 100%;
    max-width: 1400px;
    margin: 0 auto;
    align-items: center;
    justify-content: center;

    @media (max-width: 1024px) {
        width: 95%;
        flex-direction: column;
        align-items: center;
    }
`;