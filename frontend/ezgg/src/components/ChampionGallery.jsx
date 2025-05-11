import styled from '@emotion/styled';
import { ChampionPortrait } from './ChampionPortrait';

export const ChampionGallery = ({ champions }) => (
  <Gallery>
    {champions && champions.length > 0 ? (
      champions.map((champion) => (
        <ChampionPortrait key={champion} champion={champion} />
      ))
    ) : (
      <>
        <ChampionPortrait champion="Yasuo" />
        <ChampionPortrait champion="Ahri" />
        <ChampionPortrait champion="Zed" />
      </>
    )}
  </Gallery>
);

const Gallery = styled.div`
  display: flex;
  height: 200px;
  flex-shrink: 0;
  width: 100%;
  gap: 2px;
  & > * {
    flex: 1;
    min-width: 0;
  }
  @media (max-width: 768px) {
    height: 160px;
    margin: 0 -1rem;
  }
`;
