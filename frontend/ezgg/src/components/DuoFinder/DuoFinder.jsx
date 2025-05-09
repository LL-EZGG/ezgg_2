import React from 'react';
import styled from '@emotion/styled';
import {useMatchingSystem} from "../../hooks/MatchingSystem.js";
import {UserProfileCard} from "../UserProfileCard.jsx";
import {MatchingInterface} from "../MatchingInterface.jsx";

const DuoFinder = ({memberDataBundle, isLoading, userInfo}) => {
  const { isMatching, matchResult, handleMatchStart, handleMatchCancel} = useMatchingSystem();
    // const [isMatching, setIsMatching] = useState(false);
    // const [matchingCriteria, setMatchingCriteria] = useState(null);
    // const [mostPlayedChampions, setMostPlayedChampions] = useState([]);
    // const [championWinRates, setChampionWinRates] = useState({});
    // const [matchResult, setMatchResult] = useState(null);

    // memberDataBundle가 변경될 때마다 챔피언 데이터 처리
    // useEffect(() => {
    //     if (memberDataBundle && memberDataBundle.recentTwentyMatch && memberDataBundle.recentTwentyMatch.championStats) {
    //         processChampionData(memberDataBundle.recentTwentyMatch.championStats);
    //     } else if (memberDataBundle) {
    //         // 데이터는 있지만 championStats가 없는 경우
    //         setMostPlayedChampions([]);
    //         setChampionWinRates({});
    //     }
    // }, [memberDataBundle]);
    //
    // // 챔피언 데이터 처리 함수
    // const processChampionData = (championStats) => {
    //     // 챔피언 통계 객체가 있는지 확인
    //     if (!championStats || Object.keys(championStats).length === 0) {
    //         return;
    //     }
    //
    //     // championStats 객체에서 모스트 챔피언 추출 (총 게임수로 정렬)
    //     const champions = Object.values(championStats)
    //         .sort((a, b) => b.total - a.total)
    //         .slice(0, 3);
    //
    //     // 챔피언 이름 배열 추출
    //     const championNames = champions.map(champ => champ.championName);
    //     setMostPlayedChampions(championNames);
    //
    //     // 각 챔피언의 승률 추출
    //     const winRates = {};
    //     champions.forEach(champ => {
    //         winRates[champ.championName] = champ.winRateOfChampion;
    //     });
    //     setChampionWinRates(winRates);
    // };
    //
    // // 데이터가 없는 경우 기본 데이터 활용
    // const hasValidData = memberDataBundle && memberDataBundle.memberInfo;
    // const dataLoadError = !hasValidData && !isLoading;
    //
    // const handleSubmit = async (matchingCriteria) => {
    //     setMatchingCriteria(matchingCriteria);
    //
    //     try {
    //         setIsMatching(true);
    //     } catch (error) {
    //         alert('매칭에 실패하였습니다.');
    //         console.error('Matching error:', error);
    //         setIsMatching(false);
    //     }
    // };

    return (
      <Container>
        <UserProfileCard
          userInfo={userInfo}
          memberDataBundle={memberDataBundle}
          isLoading={isLoading}
        />

        <MatchingInterface
          isMatching={isMatching}
          matchResult={matchResult}
          onMatchStart={handleMatchStart}
          onCancel={handleMatchCancel}
        />
      </Container>
    )
    // return (
    //     <Container>
    //         <ProfileCard>
    //             {isLoading ? (
    //                 <LoadingSpinner>회원정보를 불러오는 중...</LoadingSpinner>
    //             ) : (
    //                 <>
    //                   <ChampionGallery champion = {mostPlayedChampions} />
    //                   <ProfileInfo>
    //                       <ProfileTitle>
    //                           {userInfo?.riotUsername || "사용자"}#{userInfo?.riotTag || "0000"}
    //                       </ProfileTitle>
    //                       <RankBadge
    //                         tier={memberDataBundle?.memberInfo?.tier}
    //                         tierNum={memberDataBundle?.memberInfo?.tierNum}
    //                       />
    //                       <WinRateStats
    //                         stats={championWinRates}
    //                         champions={mostPlayedChampions}
    //                       />
    //                   </ProfileInfo>
    //                 </>
    //             )}
    //             {!isLoading && !hasValidData && dataLoadError && (
    //                 <ErrorMessage>회원정보를 찾을 수 없습니다.</ErrorMessage>
    //             )}
    //         </ProfileCard>
    //         <FormContainer>
    //             {matchResult || isMatching ? (
    //                 <MatchResult
    //                     criteria={matchingCriteria}
    //                     matchResult={matchResult}
    //                     onCancel={() => {
    //                         setMatchResult(null);
    //                         setIsMatching(false);
    //                     }}
    //                 />
    //             ) : (
    //                 <DuoFinderForm
    //                     onSubmit={handleSubmit}
    //                     setMatchResult={setMatchResult}
    //                     setIsMatching={setIsMatching}
    //                 />
    //             )}
    //         </FormContainer>
    //     </Container>
    // );
};

export default DuoFinder;

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
