import React, {useEffect} from 'react';
import styled from '@emotion/styled';

const Container = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2rem;
`;

const LoadingSpinner = styled.div`
  width: 50px;
  height: 50px;
  border: 5px solid rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  border-top-color: #FF416C;
  animation: spin 1s ease-in-out infinite;

  @keyframes spin {
    to {
      transform: rotate(360deg);
    }
  }
`;

const Message = styled.div`
  color: white;
  font-size: 1.2rem;
  text-align: center;
`;

const CancelButton = styled.button`
  padding: 0.8rem 2rem;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: rgba(255, 255, 255, 0.2);
  }
`;

const CriteriaList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
  background: rgba(255, 255, 255, 0.05);
  padding: 1.5rem;
  border-radius: 8px;
  width: 100%;
`;

const CriteriaItem = styled.div`
  color: white;
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);

  &:last-child {
    border-bottom: none;
  }
`;

const PlayerInfo = styled.div`
  width: 100%;
  margin-bottom: 1rem;
`;

const PlayerTitle = styled.h3`
  color: white;
  margin-bottom: 1rem;
  text-align: center;
`;

const MatchResult = ({ criteria, matchResult, onCancel }) => {
  useEffect(() => {
    console.log('matchResult : ', matchResult)
    if (matchResult) {
      console.log('matchResult : ', matchResult.memberInfoDto)
    }
  }, [matchResult]);

  if (!matchResult) {
    return (
      <Container>
        <LoadingSpinner />
        <Message>듀오를 찾는 중입니다...</Message>
        <CriteriaList>
          <CriteriaItem>
            <span>내 선호 라인:</span>
            <span>{criteria.wantLine.myLine}</span>
          </CriteriaItem>
          <CriteriaItem>
            <span>상대방 선호 라인:</span>
            <span>{criteria.wantLine.partnerLine}</span>
          </CriteriaItem>
          <CriteriaItem>
            <span>선호 챔피언:</span>
            <span>{criteria.championInfo.preferredChampion || '없음'}</span>
          </CriteriaItem>
          <CriteriaItem>
            <span>비선호 챔피언:</span>
            <span>{criteria.championInfo.unpreferredChampion || '없음'}</span>
          </CriteriaItem>
        </CriteriaList>
        <CancelButton onClick={onCancel}>
            매칭 취소
        </CancelButton>
      </Container>
    );
    } else {
        return (
            <Container>
                <CriteriaList>
                    <CriteriaItem>
                        <span>상대 닉네임:</span>
                        <span>{matchResult.memberInfoDto.riotUsername} #{matchResult.memberInfoDto.riotTag}</span>
                    </CriteriaItem>
                    <CriteriaItem>
                        <span>상대 시즌 정보:</span>
                        <span>{matchResult.memberInfoDto.tier}</span>
                    </CriteriaItem>
                    <CriteriaItem>
                        <span>승률 :</span>
                        <p>{parseInt((matchResult.memberInfoDto.wins / (matchResult.memberInfoDto.wins + matchResult.memberInfoDto.losses) * 100))} %</p>
                    </CriteriaItem>
                    <CriteriaItem>
                        <span>상대 선호 챔피언:</span>
                        <span>{matchResult.preferredChampion || '없음'}</span>
                    </CriteriaItem>
                </CriteriaList>
        <CancelButton onClick={onCancel}>
          매칭 취소
        </CancelButton>
      </Container>
    );
  }
};

export default MatchResult; 