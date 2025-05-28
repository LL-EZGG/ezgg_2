import styled from '@emotion/styled';
import {ChampionPortrait} from './ChampionPortrait.jsx';

export const ChampionGallery = ({champions}) => (
    <Gallery>
        {
            champions && Object.keys(champions).length > 0 ? (
                Object.values(champions).map((champion) => (
                    <ChampionPortrait key={champion.championName} champion={champion.championName}/>
                ))
            ) : (
                <>
                    <ChampionPortrait champion="basic_1"/>
                    <ChampionPortrait champion="basic_2"/>
                    <ChampionPortrait champion="basic_3"/>
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
`;
