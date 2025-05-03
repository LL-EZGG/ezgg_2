import React from 'react';
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

const MatchResult = ({ criteria, matchResult, onCancel }) => {
    if (!matchResult) {
        return (
            <Container>
                <LoadingSpinner />
                <Message>듀오를 찾는 중입니다...</Message>

                <CriteriaList>
                    <CriteriaItem>
                        <span>내 선호 라인:</span>
                        <span>{criteria.preferredLane}</span>
                    </CriteriaItem>
                    <CriteriaItem>
                        <span>상대방 선호 라인:</span>
                        <span>{criteria.partnerLane}</span>
                    </CriteriaItem>
                    <CriteriaItem>
                        <span>선호 챔피언:</span>
                        <span>{criteria.championInfo.preferredChampions || '없음'}</span>
                    </CriteriaItem>
                    <CriteriaItem>
                        <span>비선호 챔피언:</span>
                        <span>{criteria.championInfo.bannedChampions || '없음'}</span>
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
                <SuccessBanner>🎉 매칭이 성공했습니다!</SuccessBanner>

                <CriteriaList>
                    <CriteriaItem>
                        <span>상대 닉네임:</span>
                        <span>{matchResult.nickname}</span>
                    </CriteriaItem>
                    <CriteriaItem>
                        <span>상대 선호 라인:</span>
                        <span>{matchResult.preferredLane}</span>
                    </CriteriaItem>
                    <CriteriaItem>
                        <span>상대 선호 챔피언:</span>
                        <span>{matchResult.preferredChampion || '없음'}</span>
                    </CriteriaItem>
                </CriteriaList>

                <CancelButton onClick={onCancel}>
                    돌아가기
                </CancelButton>
            </Container>
        );
    }

};

export default MatchResult; 