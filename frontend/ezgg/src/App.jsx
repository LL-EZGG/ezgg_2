import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link, Navigate, useLocation } from 'react-router-dom';
import styled from '@emotion/styled';
import DuoFinder from './components/DuoFinder';
import Login from './components/layout/Login';
import Join from './components/layout/Join';

const AppContainer = styled.div`
  min-height: 100vh;
  background: #0F0F0F;
`;

const Header = styled.header`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
`;

const Logo = styled.div`
  display: flex;
  align-items: center;
  gap: 0.5rem;
  
  h1 {
    color: white;
    font-size: 1.5rem;
    font-weight: 800;
  }
`;

const LogoIcon = styled.div`
  width: 28px;
  height: 28px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  
  svg {
    width: 18px;
    height: 18px;
    fill: black;
  }
`;

const UserSection = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;
`;

const UserInfo = styled.div`
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: white;
  cursor: pointer;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.1);
  transition: background 0.2s;

  &:hover {
    background: rgba(255, 255, 255, 0.2);
  }
`;

const LoginButton = styled(Link)`
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: white;
  cursor: pointer;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  background: #FF416C;
  text-decoration: none;
  transition: opacity 0.2s;

  &:hover {
    opacity: 0.9;
  }
`;

// 로그인 상태에 따라 리다이렉트하는 보호된 라우트 컴포넌트
const ProtectedRoute = ({ element, isLoggedIn }) => {
  const location = useLocation();
  
  if (!isLoggedIn) {
    // 로그인되지 않은 경우 로그인 페이지로 리다이렉트하면서 원래 가려던 경로 저장
    return <Navigate to="/login" state={{ from: location }} replace />;
  }
  
  return element;
};

const App = () => {
  // TODO: 실제 로그인 상태 관리 구현
  const isLoggedIn = false;
  const userInfo = {
    name: 'Hide on bush',
    tag: 'KR1'
  };

  return (
    <Router>
      <AppContainer>
        <Header>
          <a href="/">
          <Logo>
            <LogoIcon>
              <svg viewBox="0 0 24 24">
                <path d="M21.58,16.09l-1.09-7.66C20.21,6.46,18.52,5,16.53,5H7.47C5.48,5,3.79,6.46,3.51,8.43l-1.09,7.66 C2.2,17.63,3.39,19,4.94,19h0c0.68,0,1.32-0.27,1.8-0.75L9,16h6l2.25,2.25c0.48,0.48,1.13,0.75,1.8,0.75h0 C20.61,19,21.8,17.63,21.58,16.09z M11,11H9v2H8v-2H6v-1h2V8h1v2h2V11z M15,10c-0.55,0-1-0.45-1-1c0-0.55,0.45-1,1-1s1,0.45,1,1 C16,9.55,15.55,10,15,10z M17,13c-0.55,0-1-0.45-1-1c0-0.55,0.45-1,1-1s1,0.45,1,1C18,12.55,17.55,13,17,13z"/>
              </svg>
            </LogoIcon>
            <h1>EZGG</h1>
          </Logo>
          </a>
          <UserSection>
            {isLoggedIn ? (
              <UserInfo>
                {userInfo.name} #{userInfo.tag}
              </UserInfo>
            ) : (
              <LoginButton to="/login">
                Login
              </LoginButton>
            )}
          </UserSection>
        </Header>
        <Routes>
          <Route path="/" element={<ProtectedRoute element={<DuoFinder />} isLoggedIn={isLoggedIn} />} />
          <Route path="/login" element={<Login />} />
          <Route path="/join" element={<Join />} />
        </Routes>
      </AppContainer>
    </Router>
  );
};

export default App;
