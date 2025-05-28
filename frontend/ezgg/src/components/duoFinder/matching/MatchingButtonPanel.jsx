import styled from '@emotion/styled';

export const MatchingButtonPanel = (
  {
    matchingCriteria,
    matchResult,
    isMatching,
    onStart,
    onCancel
  }) => (
  <Panel>
    {!(matchingCriteria.wantLine.myLine && matchingCriteria.wantLine.partnerLine && isMatching) && !matchResult && (
      <button onClick={onStart}>매칭 시작</button>
    )}
    {(isMatching && matchingCriteria.wantLine.myLine && matchingCriteria.wantLine.partnerLine) && !matchResult && (
      <button onClick={onCancel}>매칭 중지</button>
    )}
    {matchResult && (
      <button onClick={onCancel}>돌아가기</button>
    )}
  </Panel>
);

const Panel = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 150px;
  width: 100vw;
`;
