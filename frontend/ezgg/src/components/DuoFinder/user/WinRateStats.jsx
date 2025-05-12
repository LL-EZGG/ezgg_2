import styled from '@emotion/styled';

export const WinRateStats = ({ stats, champions }) => (
  <StatsContainer>
    <StatItem>승률: {stats?.winRate || "0"}%</StatItem>
    {champions?.length > 0 ? (
      champions.map((champion, index) => (
        <StatItem key={index}>
          {champion}: {stats[champion] || "0"}% 승률
        </StatItem>
      ))
    ) : (
      <StatItem>챔피언 통계가 없습니다.</StatItem>
    )}
  </StatsContainer>
);

const StatsContainer = styled.div`
  padding: 1rem;
  background: #2a2a2a;
  border-radius: 8px;
  margin-top: 1rem;
`;

const StatItem = styled.p`
  margin: 0.5rem 0;
  font-size: 0.9rem;
  color: #e0e0e0;

  &:first-child {
    font-weight: bold;
    color: #ffd700;
  }
`;
