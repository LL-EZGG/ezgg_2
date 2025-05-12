import styled from '@emotion/styled';
import { getRankImageSrc, formatTierText } from '../../../utils/rankUtils.js';

export const RankBadge = ({ tier, tierNum }) => (
  <RankBadgeContainer>
    <img
      src={getRankImageSrc(tier)}
      alt={tier || "Unranked"}
      aria-label={formatTierText(tier, tierNum)}
    />
    <span>{formatTierText(tier, tierNum)}</span>
  </RankBadgeContainer>
);

const RankBadgeContainer = styled.div`
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
`;
