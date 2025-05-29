import {ChampionGallery} from "./champion/ChampionGallery.jsx";
import {RankBadge} from "./RankBadge.jsx";
import {WinRateStats} from "./WinRateStats.jsx";
import {LoadingSpinner} from "../../layout/LoadingSpinner.jsx";
import styled from '@emotion/styled';

export const UserProfileCard = ({ userInfo, memberDataBundle, isLoading }) => {
  const { memberInfoDto, recentTwentyMatchDto } = memberDataBundle || {};
  const effectiveMemberInfo = memberInfoDto || userInfo;

  const handleCopyClick = () => {
    const riotId = `${effectiveMemberInfo?.riotUsername || "사용자"}#${effectiveMemberInfo?.riotTag || "0000"}`;
    navigator.clipboard.writeText(riotId);
  };

  return (
    <ProfileCard>
      {isLoading ? (
        <LoadingSpinner type={'user'}/>
      ) : (
        <>
          <ChampionGallery champions = {recentTwentyMatchDto?.championStats} />
          <ProfileInfo>
            <ProfileHeader>
              <ProfileTitle>
                {effectiveMemberInfo?.riotUsername || "사용자"} #{effectiveMemberInfo?.riotTag || "0000"}
              </ProfileTitle>
              <CopyButton onClick={handleCopyClick}>
                복사
              </CopyButton>
            </ProfileHeader>
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

const ProfileHeader = styled.div`
    display: flex;
    align-items: center;
    padding: 1rem 1.5rem 0;
    gap: 1rem;
`;

const ProfileTitle = styled.h3`
    font-size: 1.6rem;
    color: white;
    font-weight: 800;
`;

const CopyButton = styled.button`
    background: rgba(255, 255, 255, 0.1);
    color: white;
    border: none;
    padding: 0.5rem 1rem;
    border-radius: 6px;
    cursor: pointer;
    font-size: 0.9rem;
    transition: background-color 0.2s;

    &:hover {
        background: rgba(255, 255, 255, 0.2);
    }
`;
