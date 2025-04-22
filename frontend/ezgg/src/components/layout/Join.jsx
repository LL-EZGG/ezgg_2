import { useState } from 'react';
import axios from 'axios';
import '../../styles/Join.css';
import { useNavigate, Link } from 'react-router-dom';

function Join() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    memberUsername: '',
    password: '',
    email: '',
    riotUsername: '',
    riotTag: ''
  });
  
  const [message, setMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log('회원가입 데이터:', formData);
    
    setIsLoading(true);
    setMessage('');
    
    try {
      const response = await axios.post('/api/auth/signup', formData);
      console.log('회원가입 성공:', response.data);
      setMessage('회원가입이 성공적으로 완료되었습니다!');
      navigate("/");
    } catch (error) {
      console.error('회원가입 오류:', error);
      setMessage(error.response?.data?.message || '회원가입 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="auth-form-container">
      <Link to="/" className="auth-logo-link">
        <img src="/logo.png" alt="EZGG Logo" className="auth-logo-image" />
      </Link>
      <form onSubmit={handleSubmit} className="auth-form">
        {message && <div className={`message ${message.includes('성공') ? 'success' : 'error'}`}>{message}</div>}
        
        <input
          type="text"
          name="memberUsername"
          placeholder="ID"
          value={formData.memberUsername}
          onChange={handleChange}
          required
          disabled={isLoading}
        />
        
        <input
          type="password"
          name="password"
          placeholder="PW"
          value={formData.password}
          onChange={handleChange}
          required
          disabled={isLoading}
        />
        
        <input
          type="email"
          name="email"
          placeholder="Email"
          value={formData.email}
          onChange={handleChange}
          required
          disabled={isLoading}
        />
        
        <input
          type="text"
          name="riotUsername"
          placeholder="Riot ID"
          value={formData.riotUsername}
          onChange={handleChange}
          required
          disabled={isLoading}
        />
        
        <input
          type="text"
          name="riotTag"
          placeholder="Tag"
          value={formData.riotTag}
          onChange={handleChange}
          required
          disabled={isLoading}
        />
        
        <button type="submit" className="auth-button" disabled={isLoading}>
          {isLoading ? '처리 중...' : '회원가입'}
        </button>
        <Link to="/" className="home-link">홈으로</Link>
      </form>
    </div>
  );
}

export default Join; 