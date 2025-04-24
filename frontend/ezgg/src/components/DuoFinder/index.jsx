import React, { useState } from 'react';
import styled from '@emotion/styled';
import DuoFinderForm from './DuoFinderForm';
import MatchResult from './MatchResult';

const Container = styled.div`
  display: flex;
  gap: 2rem;
  padding: 2rem;
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  min-height: calc(100vh - 4rem);
  align-items: center;
  justify-content: center;

  @media (max-width: 1024px) {
    width: 90%;
    flex-direction: column;
    align-items: stretch;
  }

  @media (max-width: 768px) {
    width: 95%;
    padding: 1rem;
    gap: 1rem;
  }
`;

const ProfileCard = styled.div`
  flex: 1;
  width: 100%;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  flex-direction: column;

  @media (max-width: 1024px) {
    max-width: 100%;
  }
`;

const ChampionImages = styled.div`
  display: flex;
  height: 150px;
  flex-shrink: 0;
  
  img {
    width: 33.333%;
    height: 100%;
    object-fit: cover;
  }

  @media (max-width: 768px) {
    height: 120px;
  }
`;

const ProfileInfo = styled.div`
  padding: 1.5rem;
  color: white;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;

  @media (max-width: 768px) {
    padding: 1rem;
  }
`;

const ProfileTitle = styled.h3`
  font-size: 1.2rem;
  margin-bottom: 1rem;
  color: white;
`;

const RankBadge = styled.div`
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1rem;
  
  img {
    width: 40px;
    height: 40px;
  }

  span {
    color: white;
    font-size: 1.1rem;
  }
`;

const Stats = styled.div`
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.7);
  
  p {
    margin: 0.3rem 0;
  }
`;

const FormContainer = styled.div`
  flex: 1;
  width: 100%;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 2rem;
  display: flex;
  flex-direction: column;

  @media (max-width: 1024px) {
    max-width: 100%;
  }

  @media (max-width: 768px) {
    padding: 1rem;
  }
`;

const DuoFinder = () => {
  const [matchingCriteria, setMatchingCriteria] = useState(null);
  const [isMatching, setIsMatching] = useState(false);

  const handleSubmit = (criteria) => {
    setMatchingCriteria(criteria);
    setIsMatching(true);
    // TODO: API 호출 로직 추가
  };

  return (
    <Container>
      <ProfileCard>
        <ChampionImages>
          <img src="/champions/Yasuo.png" alt="Yasuo" />
          <img src="/champions/Ahri.png" alt="Ahri" />
          <img src="/champions/Zed.png" alt="Zed" />
        </ChampionImages>
        <ProfileInfo>
          <ProfileTitle>Hide on bush #KR1</ProfileTitle>
          <RankBadge>
            <img src="/ranks/grandmaster.png" alt="Grandmaster" />
            <span>Grandmaster 1</span>
          </RankBadge>
          <Stats>
            <p>최근 20경기 K/D/A 7.4/3.9/6 승률 65%</p>
            <p>S2024 S3 K/D/A 7.4/3.9/6 승률 65%</p>
          </Stats>
        </ProfileInfo>
      </ProfileCard>
      <FormContainer>
        {!isMatching ? (
          <DuoFinderForm onSubmit={handleSubmit} />
        ) : (
          <MatchResult 
            criteria={matchingCriteria}
            onCancel={() => setIsMatching(false)}
          />
        )}
      </FormContainer>
    </Container>
  );
};

export default DuoFinder; 