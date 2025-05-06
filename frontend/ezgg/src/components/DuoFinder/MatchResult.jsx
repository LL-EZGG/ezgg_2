import React, {useEffect, useState} from 'react';
import styled from '@emotion/styled';

const Container = styled.div`
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

const LoadingSpinner = styled.div`
  width: 50px;
  height: 50px;
  border: 5px solid rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  border-top-color: #FF416C;
  animation: spin 1s ease-in-out infinite;

  @keyframes spin {
    to {
      transform: rotate(360deg);
    }
  }
`;

const Message = styled.div`
  color: white;
  font-size: 1.2rem;
  text-align: center;
`;

const CancelButton = styled.button`
  padding: 0.8rem 2rem;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: rgba(255, 255, 255, 0.2);
  }
`;

const ChampionImages = styled.div`
  display: flex;
  height: 200px;
  flex-shrink: 0;
  width: 100%;

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
  justify-content: flex-start;
  gap: 1.7rem;
  width: 100%;
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
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  width: 100%;

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

const MatchResult = ({ criteria, matchResult, onCancel }) => {
  const [mostPlayedChampions, setMostPlayedChampions] = useState([]);
  const [championWinRates, setChampionWinRates] = useState({});

  const getChampionImageSrc = (championName) => {
    if (!championName) return "/champions/default.png";

    // 특수 케이스 처리 (공백이나 특수문자가 있는 챔피언)
    const specialCases = {
      'missfortune': 'MissFortune',
      'drmundo': 'DrMundo',
      'jarvaniv': 'JarvanIV',
      'leesin': 'LeeSin',
      'masteryi': 'MasterYi',
      'tahmkench': 'TahmKench',
      'twistedfate': 'TwistedFate',
      'xinzhao': 'XinZhao',
      'aurelionsol': 'AurelionSol',
      'kogmaw': 'KogMaw',
      'reksai': 'RekSai'
    };

    // 소문자로 변환하여 특수 케이스 체크
    const lowerName = championName.toLowerCase();
    if (specialCases[lowerName]) {
      return `/champions/${specialCases[lowerName]}.png`;
    }

    // 일반적인 경우: 첫 글자만 대문자로, 나머지는 소문자로 변환
    const formattedName = championName.charAt(0).toUpperCase() + championName.slice(1).toLowerCase();
    return `/champions/${formattedName}.png`;
  };

  const getRankImageSrc = (tier) => {
    const tierLower = (tier || "").toLowerCase();
    const validTiers = ["iron", "bronze", "silver", "gold", "platinum", "diamond", "master", "grandmaster", "challenger"];

    if (validTiers.includes(tierLower)) {
      return `/ranks/${tierLower}.png`;
    }
    return "/ranks/unranked.png";
  };

  // 챔피언 데이터 처리 함수
  const processChampionData = (championStats) => {
    if (!championStats || Object.keys(championStats).length === 0) {
      return;
    }

    // championStats 객체를 배열로 변환하고 total 기준으로 정렬
    const champions = Object.values(championStats)
      .sort((a, b) => b.total - a.total)
      .slice(0, 3);

    const championNames = champions.map(champ => champ.championName);
    setMostPlayedChampions(championNames);

    const winRates = {};
    champions.forEach(champ => {
      winRates[champ.championName] = champ.winRateOfChampion;
    });
    setChampionWinRates(winRates);
  };

  useEffect(() => {
    console.log('matchResult : ', matchResult)
    if (matchResult) {
      console.log('matchResult : ', matchResult.memberInfoDto)
      if (matchResult.recentTwentyMatchDto && matchResult.recentTwentyMatchDto.championStats) {
        processChampionData(matchResult.recentTwentyMatchDto.championStats);
      }
    }
  }, [matchResult]);

  if (!matchResult) {
    return (
      <Container>
        <LoadingSpinner />
        <Message>듀오를 찾는 중입니다...</Message>
        <Stats>
          <p>내 선호 라인: {criteria.wantLine.myLine}</p>
          <p>상대방 선호 라인: {criteria.wantLine.partnerLine}</p>
          <p>선호 챔피언: {criteria.championInfo.preferredChampion || '없음'}</p>
          <p>비선호 챔피언: {criteria.championInfo.unpreferredChampion || '없음'}</p>
        </Stats>
        <CancelButton onClick={onCancel}>
          매칭 취소
        </CancelButton>
      </Container>
    );
  } else {
    return (
      <Container>
        <ChampionImages>
          {mostPlayedChampions && mostPlayedChampions.length > 0 ? (
            mostPlayedChampions.map((champion, index) => (
              <img 
                key={index} 
                src={getChampionImageSrc(champion)} 
                alt={champion || "Champion"}
              />
            ))
          ) : (
            <>
              <img src="/champions/Yasuo.png" alt="Default Champion 1"/>
              <img src="/champions/Ahri.png" alt="Default Champion 2"/>
              <img src="/champions/Zed.png" alt="Default Champion 3"/>
            </>
          )}
        </ChampionImages>
        <ProfileInfo>
          <ProfileTitle>
            {matchResult.memberInfoDto.riotUsername}#{matchResult.memberInfoDto.riotTag}
          </ProfileTitle>
          <RankBadge>
            <img
              src={getRankImageSrc(matchResult.memberInfoDto.tier)}
              alt={matchResult.memberInfoDto.tier || "Unranked"}
            />
            <span>{matchResult.memberInfoDto.tier || "Unranked"} {matchResult.memberInfoDto.tierNum || ""}</span>
          </RankBadge>
          <Stats>
            <p>승률: {matchResult?.recentTwentyMatchDto?.winRate || "0"}%</p>
            {mostPlayedChampions && mostPlayedChampions.length > 0 ? (
              mostPlayedChampions.map((champion, index) => (
                <p key={index}>
                  {champion}: {championWinRates[champion] || "0"}% 승률
                </p>
              ))
            ) : (
              <p>챔피언 통계가 없습니다.</p>
            )}
          </Stats>
        </ProfileInfo>
        <CancelButton onClick={onCancel}>
          매칭 취소
        </CancelButton>
      </Container>
    );
  }
};

export default MatchResult; 