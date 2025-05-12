import React from 'react';
import styled from '@emotion/styled';

// props: type = 'user' | 'match' | ... (확장 가능), message(직접 지정)
const messages = {
  user: '회원정보를 불러오는 중...',
  match: '매칭 시도 중...',
};

export const LoadingSpinner = ({ type }) => (
  <LoadingContainer> {messages[type] || '로딩 중...'}</LoadingContainer>
);

const LoadingContainer = styled.div`
    display: flex;
    justify-content: center;
    align-items: center;
    flex: 1;
    color: white;
    font-size: 1.2rem;
`;