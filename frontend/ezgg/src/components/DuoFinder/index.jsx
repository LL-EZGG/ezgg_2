import React, { useState } from 'react';
import styled from '@emotion/styled';
import DuoFinderForm from './DuoFinderForm';
import MatchResult from './MatchResult';
import axios from "axios";

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
  min-height: 635px;
  padding: 2rem;

  @media (max-width: 1024px) {
    max-width: 100%;
  }

  @media (max-width: 768px) {
    padding: 1rem;
  }
`;

const ChampionImages = styled.div`
  display: flex;
  height: 200px;
  flex-shrink: 0;
  // margin: -2rem -2rem 0;
  
  img {
    width: 33.333%;
    height: 200px;
    object-fit: cover;
  }

  @media (max-width: 768px) {
    height: 160px;
    margin: -1rem -1rem 0;
    
    img {
      height: 160px;
    }
  }
`;

const ProfileInfo = styled.div`
  padding: 2rem 0 0;
  color: white;
  flex: 1;
  display: flex;
  flex-direction: column;
  // justify-content: space-between;
  justify-content: flex-start;
  gap: 1.7rem;
`;

const ProfileTitle = styled.h3`
  font-size: 1.6rem;
  color: white;
  padding: 0 1.5rem;
  font-weight: 800;

  @media (max-width: 768px) {
    font-size: 1.2rem;
    padding: 0 1rem;
    margin-bottom: 1.5rem;
  }
`;

const RankBadge = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0 1.5rem;
  border-radius: 8px;
  
  img {
    width: 60px;
    height: 60px;
  }

  span {
    color: white;
    font-size: 1.5rem;
    font-weight: 500;
  }

  @media (max-width: 768px) {
    padding: 1rem;
    gap: 0.8rem;
    
    img {
      width: 50px;
      height: 50px;
    }

    span {
      font-size: 1.2rem;
    }
  }
`;

const Stats = styled.div`
  font-size: 1rem;
  color: rgba(255, 255, 255, 0.7);
  margin-top: auto;
  padding: 0 1.5rem;
  
  p {
    margin: 0.8rem 0;
  }

  @media (max-width: 768px) {
    font-size: 0.9rem;
    padding: 0 1rem;
    
    p {
      margin: 0.5rem 0;
    }
  }
`;

const FormContainer = styled.div`
  flex: 1;
  width: 100%;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 2rem;
  display: flex;
  min-height: 635px;
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

  const handleSubmit = async (matchingCriteria) => {
    console.log(matchingCriteria);
    const token = localStorage.getItem('token');
    console.log(token);
    setMatchingCriteria(matchingCriteria);
    // const [matchedUser, setMatchedUser] = useState(null);
    // useEffect(() => {
    //   const savedCriteria = localStorage.getItem('matchingCriteria');
    //   if (savedCriteria) {
    //     const parsed = JSON.parse(savedCriteria);
    //     setMatchingCriteria(parsed);
    //     setIsMatching(true);
    //   }
    // }, []);
    try {
      const response = await axios.post(
          'http://localhost:8888/matching/start',
          {
            wantLine: {
              myLine: matchingCriteria.preferredLane,
              partnerLine: matchingCriteria.partnerLane,
            },
            championInfo: {
              preferredChampion: matchingCriteria.preferredChampions[0]?.name || '',
              unpreferredChampion: matchingCriteria.bannedChampions[0]?.name || '',
            },
          },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
            withCredentials: true,
          }
      );

      const newToken = response.headers['authorization'];
      setIsMatching(true);
      if (response.status === 200 && newToken) {
        localStorage.setItem('token', newToken);

      }
    } catch (error) {
      alert('매칭에 실패하였습니다.');
      console.error('Matching error:', error);
      setIsMatching(false);
    }
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