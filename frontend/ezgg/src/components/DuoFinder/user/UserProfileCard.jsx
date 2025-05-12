import {ChampionGallery} from "./champion/ChampionGallery.jsx";
import {RankBadge} from "./RankBadge.jsx";
import {WinRateStats} from "./WinRateStats.jsx";
import {LoadingSpinner} from "../../layout/LoadingSpinner.jsx";
import styled from '@emotion/styled';
import React from "react";

export const UserProfileCard = ({ userInfo, memberDataBundle, isLoading }) => {
  const { mostPlayedChampions, championWinRates } = memberDataBundle || {};

  return (
    <ProfileCard>
      {isLoading ? (
        <LoadingSpinner type={'user'}/>
      ) : (
        <>
          <ChampionGallery champion = {mostPlayedChampions} />
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
