import React, {useEffect, useState} from 'react';
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

const DuoFinder = ({ memberDataBundle, isLoading }) => {
    const [isMatching, setIsMatching] = useState(false);
    const [matchingCriteria, setMatchingCriteria] = useState(null);
    const [mostPlayedChampions, setMostPlayedChampions] = useState([]);
    const [championWinRates, setChampionWinRates] = useState({});

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

    const handleSubmit = (criteria) => {
        setMatchingCriteria(criteria);
        setIsMatching(true);
    };

    const getRankImageSrc = (tier) => {
        // 티어에 따른 이미지 경로 반환
        const tierLower = (tier || "").toLowerCase();
        const validTiers = ["iron", "bronze", "silver", "gold", "platinum", "diamond", "master", "grandmaster", "challenger"];

        if (validTiers.includes(tierLower)) {
            return `/ranks/${tierLower}.png`;
        }

        return "/ranks/unranked.png";
    };

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

    // 데이터가 없는 경우 기본 데이터 활용
    const hasValidData = memberDataBundle && memberDataBundle.memberInfo;
    const dataLoadError = !hasValidData && !isLoading;

    return (
        <Container>
            <ProfileCard>
                {isLoading ? (
                    <LoadingSpinner>회원정보를 불러오는 중...</LoadingSpinner>
                ) : hasValidData ? (
                    <>
                        <ChampionImages>
                            {mostPlayedChampions && mostPlayedChampions.length > 0 ? (
                                mostPlayedChampions.map((champion, index) => (
                                    <img key={index} src={getChampionImageSrc(champion)} alt={champion || "Champion"}/>
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
                                {memberDataBundle?.memberInfo?.riotUsername || "사용자"}#{memberDataBundle?.memberInfo?.riotTag || "0000"}
                            </ProfileTitle>
                            <RankBadge>
                                <img
                                    src={getRankImageSrc(memberDataBundle?.memberInfo?.tier)}
                                    alt={memberDataBundle?.memberInfo?.tier || "Unranked"}
                                />
                                <span>{memberDataBundle?.memberInfo?.tier || "Unranked"} {memberDataBundle?.memberInfo?.tierNum || ""}</span>
                            </RankBadge>
                            <Stats>
                                <p>승률: {memberDataBundle?.recentTwentyMatch?.winRate || "0"}%</p>
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
                    </>
                ) : (
                    <ErrorMessage>
                        {dataLoadError ? "회원정보를 찾을 수 없습니다." : ""}
                    </ErrorMessage>
                )}
            </ProfileCard>
            <FormContainer>
                {!isMatching ? (
                    <DuoFinderForm onSubmit={handleSubmit}/>
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
