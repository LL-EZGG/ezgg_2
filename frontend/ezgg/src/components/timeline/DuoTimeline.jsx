import { useState } from 'react';
import styled from '@emotion/styled';
import {testJson} from '../../utils/tst.js';

// 유틸: ms → mm:ss
const formatTime = (timestamp) => {
  const seconds = Math.floor(timestamp / 1000);
  const minutes = Math.floor(seconds / 60);
  const remain = seconds % 60;
  return `${minutes}:${remain.toString().padStart(2, '0')}`;
};

const getUsernameById = (participantId, userMatchInfos = []) => {
  if (!userMatchInfos[participantId] || userMatchInfos === []) return null;
  const { riotUsername, riotTag } = userMatchInfos[participantId].timelineMemberInfoDto;

  return `${riotUsername} #${riotTag} `;
};

const getEventType = (event, myId, duoId) => {
  if (event.type === 'CHAMPION_KILL') {
    if (event.killerId === myId || event.killerId === duoId) return 'KILL';
    if (event.victimId === myId || event.victimId === duoId) return 'DEATH';
    if ((event.assistingParticipantIds || []).includes(myId) || (event.assistingParticipantIds || []).includes(duoId)) return 'ASSIST';
    return 'CHAMPION_KILL';
  }

  if (event.type === 'TURRET_PLATE_DESTROYED') return 'TURRET';
  if (event.type === 'BUILDING_KILL') return 'BUILDING';
  if (event.type === 'ELITE_MONSTER_KILL') return 'MONSTER';

  return 'ETC';
};

const getEventSummary = (event, myId, duoId, userMatchInfos) => {
  const { killerId, victimId, assistingParticipantIds = [] } = event;

  switch (event.type) {
    case 'CHAMPION_KILL':
      if (killerId === myId || killerId === duoId) return `⚔️ ${getUsernameById(killerId, userMatchInfos)}: 킬`;
      if (victimId === myId || victimId === duoId) return `💀 ${getUsernameById(victimId, userMatchInfos)}: 죽음`;
      if (assistingParticipantIds.includes(myId) || assistingParticipantIds.includes(duoId)) {
        const assists = assistingParticipantIds
          .filter(id => id === myId || id === duoId)
          .map(id => getUsernameById(id, userMatchInfos));
        return `🧩 ${assists.join(', ')}: 어시스트`;
      }
      break;
    case 'ELITE_MONSTER_KILL':
      if (killerId === myId || killerId === duoId) return `🐉 ${getUsernameById(killerId, userMatchInfos)}: 몬스터 처치`;
      break;
    case 'BUILDING_KILL':
      if (killerId === myId || killerId === duoId) return `🏰 ${getUsernameById(killerId, userMatchInfos)}: 건물 파괴`;
      break;
    case 'TURRET_PLATE_DESTROYED':
      if (killerId === myId || killerId === duoId) return `🪙 ${getUsernameById(killerId, userMatchInfos)}: 포탑 골드`;
      break;
    default:
      return null;
  }

  return null;
};

// 시간 블록
const TimelineTimeBlock = ({ timestamp, events, userMatchInfos, myId, duoId }) => {
  const time = formatTime(timestamp);
  const validEvents = (events || []).filter(e => getEventSummary(e, myId, duoId, userMatchInfos) !== null);

  if (validEvents.length === 0) return null;

  return (
    <div>
      <BlockTitle>⏰ {time}</BlockTitle>
      {validEvents.map((e, j) => (
        <TimelineEvent key={j} event={e} myId={myId} duoId={duoId} userMatchInfos={userMatchInfos}
        />
      ))}
    </div>
  );
};

// 개별 이벤트 박스
const TimelineEvent = ({ event, myId, duoId, userMatchInfos }) => {
  console.log("event : ", event)
  const summary = getEventSummary(event, myId, duoId, userMatchInfos);
  if (!summary) return null;

  const type = getEventType(event, myId, duoId);
  return <EventBox type={type}>{summary}</EventBox>;
};

const TimelineGroup = ({ matchIndex, timeline, userMatchInfos, myId, duoId }) => {
  const [open, setOpen] = useState(false);

  return (
    <MatchCard>
      <MatchHeader onClick={() => setOpen(!open)}>
        <div className="title">
          <span className="icon">🕹️</span>
          <span className="text">
            <strong>{getUsernameById(myId, userMatchInfos)}</strong>
            <span className="and"> & </span>
            <strong>{getUsernameById(duoId, userMatchInfos)}</strong>
          </span>
        </div>
        <span className="toggle">{open ? '▲' : '▼'}</span>
      </MatchHeader>

      {open && (
        <TimelineBlock>
          {timeline.map((block, i) => {
            const events = block.events.filter((e) => {
              const relatedIds = [e.killerId, e.victimId, ...(e.assistingParticipantIds || [])];
              return relatedIds.some(id => id === myId || id === duoId);
            });
            if (events.length === 0) return null;
            return (
              <TimelineTimeBlock key={i} timestamp={block.timestamp} events={events} userMatchInfos={userMatchInfos} myId={myId} duoId={duoId} />
            );
          })}
        </TimelineBlock>
      )}
    </MatchCard>
  );
};

const DuoTimeline = ({ memberData, matchTimelines }) => {
  if (!memberData) {
    return <div style={{ color: 'white' }}>유저 데이터가 없습니다</div>;
  }

  const memberId = memberData.memberInfoDto.memberId;
  const userMatchInfos = testJson.data.userMatchInfos;

  const duoParticipantIds = Object.keys(userMatchInfos).sort((a, b) => {
    const aIsMine = userMatchInfos[a].timelineMemberInfoDto.memberId === memberId;
    const bIsMine = userMatchInfos[b].timelineMemberInfoDto.memberId === memberId;
    return aIsMine ? -1 : bIsMine ? 1 : 0;
  }).map(Number);

  const myParticipantId = duoParticipantIds[0];
  const duoParticipantId = duoParticipantIds[1];

  return (
    <Wrapper>
      <h2 style={{ color: 'white', fontSize: '28px', fontWeight: 'bold', marginBottom: '24px' }}>
        🎮 듀오 매치 타임라인
      </h2>
      <TimelineContainer>
        {/* be에서 10개 데이터 넘어도록 수정 후 코드 변경 */}
        {
          <TimelineGroup
            key={0}
            matchIndex={0}
            timeline={testJson.data.timeline}
            userMatchInfos={userMatchInfos}
            myId={myParticipantId}
            duoId={duoParticipantId}
          />
        }
        {/* 10개 매치 타임라인 처리 로직*/}
        {/*{matchTimelines.map((match, idx) => (*/}
        {/*  <TimelineGroup*/}
        {/*    key={idx}*/}
        {/*    matchIndex={idx}*/}
        {/*    timeline={match.timeline}*/}
        {/*    userMatchInfos={userMatchInfos}*/}
        {/*    myId={myId}*/}
        {/*    duoId={duoId}*/}
        {/*  />*/}
        {/*))}*/}
      </TimelineContainer>
    </Wrapper>
  );
};

export default DuoTimeline;


const Wrapper = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 40px 16px;
    background-color: black;
    min-height: 100vh;
    overflow-y: auto;   // 넘칠 때만 스크롤
    overscroll-behavior: contain; // 내부 스크롤이 끝난 후 외부 스크롤로 전환
`;

const TimelineContainer = styled.div`
  width: 100%;
  max-width: 720px;
`;

const MatchCard = styled.div`
  background: #1e1e1e;
  border-radius: 16px;
  margin-bottom: 24px;
  overflow: hidden;
  box-shadow: 0 0 8px rgba(255, 255, 255, 0.05);
`;

const MatchHeader = styled.div`
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin: 0 auto;
    padding: 12px 16px;
    background-color: #2c2c2c;
    font-size: 14px;
    font-weight: 500;
    color: white;
    border-radius: 12px;
    cursor: pointer;

  .title {
    display: contents;
    gap: 10px;
    overflow: hidden;
  }

  .icon {
    font-size: 16px;
    flex-shrink: 0;
  }
    
  .and {
    color: #aaa;
    font-weight: normal;
  }

  .toggle {
    font-size: 12px;
    color: #ccc;
  }
`;

const TimelineBlock = styled.div`
  padding: 16px;
  max-height: 300px;
  overflow-y: auto;
`;

const BlockTitle = styled.div`
  font-weight: bold;
  font-size: 16px;
  color: #ccc;
  margin: 12px 0;
`;

const EventBox = styled.div`
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: 500;
  ${({ type }) => {
  switch (type) {
    case 'KILL':
      return `background: #4b1e1e; color: #ffb3b3;`;
    case 'DEATH':
      return `background: #1e274b; color: #b3cfff;`;
    case 'ASSIST':
      return `background: #263341; color: #c0eaff;`;
    case 'TURRET':
      return `background: #3f2e1e; color: #ffd9b3;`;
    case 'BUILDING':
      return `background: #2f2f2f; color: #cccccc;`;
    case 'MONSTER':
      return `background: #3a1e4b; color: #e0b3ff;`;
    default:
      return `background: #333; color: white;`;
  }
}}
`;
