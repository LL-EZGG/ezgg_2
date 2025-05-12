import {ChampionGallery} from "./champion/ChampionGallery.jsx";
import {RankBadge} from "./RankBadge.jsx";
import {WinRateStats} from "./WinRateStats.jsx";
import {LoadingSpinner} from "../../layout/LoadingSpinner.jsx";
import styled from '@emotion/styled';

export const UserProfileCard = ({ userInfo, memberDataBundle, isLoading }) => {
  const { memberInfoDto, recentTwentyMatchDto } = memberDataBundle || {};

  return (
    <ProfileCard>
      {isLoading ? (
        <LoadingSpinner type={'user'}/>
      ) : (
        <>
          <ChampionGallery champions = {recentTwentyMatchDto?.championStats} />
          <ProfileInfo>
            <ProfileTitle>
              {userInfo?.riotUsername || "사용자"} #{userInfo?.riotTag || "0000"}
            </ProfileTitle>
            <RankBadge
              tier={memberInfoDto?.tier}
              tierNum={memberInfoDto?.tierNum}
            />
            <WinRateStats
              winRate={Math.round(memberInfoDto?.wins / (memberInfoDto?.wins + memberInfoDto?.losses) * 100)}
              champions={recentTwentyMatchDto?.championStats}
            />
          </ProfileInfo>
        </>
      )}
    </ProfileCard>
  )
};

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
`;

const ProfileInfo = styled.div`
    color: white;
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    gap: 0.9rem;
`;

const ProfileTitle = styled.h3`
    font-size: 1.6rem;
    color: white;
    padding: 1rem 1.5rem 0;
    font-weight: 800;
`;
