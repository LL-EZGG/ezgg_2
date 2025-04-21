import React, {useState} from 'react';
import {useNavigate, Link} from 'react-router-dom';
import axios from 'axios';
import '../../styles/Login.css';

function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const response = await axios.post('/api/login', {
                memberUsername: username,
                password: password
            });

            const token = response.headers['authorization'];
            if (response.status === 200) {
                // 로그인 성공 후, JWT 토큰을 로컬 스토리지에 저장
                localStorage.setItem('token', token);
                // 로그인 성공 후, 홈 페이지로 리다이렉트
                navigate('/');
            }
        } catch (error) {
            alert('로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.');
            console.error('Login error:', error);
        }
    };

    return (
        <div className="auth-form-container">
            <Link to="/" className="auth-logo-link">
                <img src="/logo.png" alt="EZGG Logo" className="auth-logo-image" />
            </Link>
            <form className="auth-form" onSubmit={handleSubmit}>
                <input
                    type="text"
                    placeholder="ID"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                />
                <input
                    type="password"
                    placeholder="PW"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
                <button type="submit" className="auth-button">로그인</button>
                <Link to="/" className="home-link">홈으로</Link>
            </form>
        </div>
    );
}

export default Login;
