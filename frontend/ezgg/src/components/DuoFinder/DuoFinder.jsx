import React, {useEffect, useState} from 'react';
import styled from '@emotion/styled';
import DuoFinderForm from './DuoFinderForm';
import MatchResult from './MatchResult';
import {RankBadge} from "../RankBadge.jsx";
import {ChampionPortrait} from "../ChampionPortrait.jsx";
import {WinRateStats} from "../WinRateStats.jsx";

const DuoFinder = ({memberDataBundle, isLoading, userInfo}) => {
    const [isMatching, setIsMatching] = useState(false);
    const [matchingCriteria, setMatchingCriteria] = useState(null);
    const [mostPlayedChampions, setMostPlayedChampions] = useState([]);
    const [championWinRates, setChampionWinRates] = useState({});
    const [matchResult, setMatchResult] = useState(null);

    // memberDataBundle가 변경될 때마다 챔피언 데이터 처리
    useEffect(() => {
        if (memberDataBundle && memberDataBundle.recentTwentyMatch && memberDataBundle.recentTwentyMatch.championStats) {
            processChampionData(memberDataBundle.recentTwentyMatch.championStats);
        } else if (memberDataBundle) {
            // 데이터는 있지만 championStats가 없는 경우
            setMostPlayedChampions([]);
            setChampionWinRates({});
        }
    }, [memberDataBundle]);

    // 챔피언 데이터 처리 함수
    const processChampionData = (championStats) => {
        // 챔피언 통계 객체가 있는지 확인
        if (!championStats || Object.keys(championStats).length === 0) {
            return;
        }

        // championStats 객체에서 모스트 챔피언 추출 (총 게임수로 정렬)
        const champions = Object.values(championStats)
            .sort((a, b) => b.total - a.total)
            .slice(0, 3);

        // 챔피언 이름 배열 추출
        const championNames = champions.map(champ => champ.championName);
        setMostPlayedChampions(championNames);

        // 각 챔피언의 승률 추출
        const winRates = {};
        champions.forEach(champ => {
            winRates[champ.championName] = champ.winRateOfChampion;
        });
        setChampionWinRates(winRates);
    };

    // 데이터가 없는 경우 기본 데이터 활용
    const hasValidData = memberDataBundle && memberDataBundle.memberInfo;
    const dataLoadError = !hasValidData && !isLoading;

    const handleSubmit = async (matchingCriteria) => {
        setMatchingCriteria(matchingCriteria);

        try {
            setIsMatching(true);
        } catch (error) {
            alert('매칭에 실패하였습니다.');
            console.error('Matching error:', error);
            setIsMatching(false);
        }
    };

    return (
        <Container>
            <ProfileCard>
                {isLoading ? (
                    <LoadingSpinner>회원정보를 불러오는 중...</LoadingSpinner>
                ) : (
                    <>
                      <ChampionGallery>
                        {mostPlayedChampions?.length > 0 ? (
                          mostPlayedChampions.map((champion) => (
                            <ChampionPortrait champion={champion} />
                          ))
                        ) : (
                          <>
                            <ChampionPortrait champion="Yasuo" />
                            <ChampionPortrait champion="Ahri" />
                            <ChampionPortrait champion="Zed" />
                          </>
                        )}
                      </ChampionGallery>
                      <ProfileInfo>
                          <ProfileTitle>
                              {userInfo?.riotUsername || "사용자"}#{userInfo?.riotTag || "0000"}
                          </ProfileTitle>
                          <RankBadge
                            tier={memberDataBundle?.memberInfo?.tier}
                            tierNum={memberDataBundle?.memberInfo?.tierNum}
                          />

                          <WinRateStats
                            stats={championWinRates}
                            champions={mostPlayedChampions}
                          />
                      </ProfileInfo>
                    </>
                )}
                {!isLoading && !hasValidData && dataLoadError && (
                    <ErrorMessage>회원정보를 찾을 수 없습니다.</ErrorMessage>
                )}
            </ProfileCard>
            <FormContainer>
                {matchResult || isMatching ? (
                    <MatchResult
                        criteria={matchingCriteria}
                        matchResult={matchResult}
                        onCancel={() => {
                            setMatchResult(null);
                            setIsMatching(false);
                        }}
                    />
                ) : (
                    <DuoFinderForm
                        onSubmit={handleSubmit}
                        setMatchResult={setMatchResult}
                        setIsMatching={setIsMatching}
                    />
                )}
            </FormContainer>
        </Container>
    );
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

const ProfileInfo = styled.div`
    padding: 2rem 0 0;
    color: white;
    flex: 1;
    display: flex;
    flex-direction: column;
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

const FormContainer = styled.div`
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
    display: flex;
    justify-content: center;
    align-items: center;
    flex: 1;
    color: white;
    font-size: 1.2rem;
`;

const ErrorMessage = styled.div`
    color: #ff6b6b;
    padding: 1rem;
    text-align: center;
    background: rgba(255, 0, 0, 0.1);
    border-radius: 8px;
    margin: 1rem 0;
`;

const ChampionGallery = styled.div`
    display: flex;
    height: 200px;
    flex-shrink: 0;

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
