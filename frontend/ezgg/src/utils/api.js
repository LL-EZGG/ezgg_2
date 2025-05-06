import axios from 'axios';

// axios 인스턴스 생성
const api = axios.create({
  baseURL: 'http://localhost:8888',
  timeout: 10000,
  withCredentials: true,
});

// 요청 인터셉터 설정
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      // 백엔드는 'Bearer ' 접두사가 있는 토큰을 예상
      config.headers['Authorization'] = token.startsWith('Bearer ') ? token : `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터 설정
api.interceptors.response.use(
  (response) => {
    // 성공적인 응답에서 토큰이 있으면 갱신
    const token = response.headers['authorization'];
    if (token) {
      localStorage.setItem('token', token);
    }
    return response;
  },
  (error) => {
    console.error('API 오류 발생:', error.response?.status, error.config?.url);
    
    // 토큰이 만료되었거나 유효하지 않은 경우 (401, 403 오류)
    if (error.response && 
        (error.response.status === 401 || error.response.status === 403)) {
      
      console.log('인증 오류 발생:', error.config.url);
      
      // 특정 엔드포인트에서는 인증 오류를 무시하고 로그아웃 처리를 하지 않음
      const ignoredEndpoints = [
        '/auth/memberdatabundle',
        '/auth/memberstats',  // 향후 추가될 수 있는 다른 엔드포인트도 포함
        '/auth/logout'        // 로그아웃 API 호출 시 401, 403 오류는 무시
      ];
      
      if (ignoredEndpoints.includes(error.config.url)) {
        console.log(`${error.config.url} API 오류 - 로그아웃 처리 건너뜀`);
        return Promise.reject(error);
      }
      
      // 로그인 관련 요청에서는 리다이렉트하지 않음
      if (error.config.url !== '/login') {
        console.log('토큰 오류로 로그아웃 처리');
        // 로그아웃 처리
        localStorage.removeItem('token');
        
        // 현재 페이지가 로그인 페이지가 아닌 경우에만 리다이렉트
        if (!window.location.pathname.includes('/login')) {
          console.log('로그인 페이지로 리다이렉트');
          window.location.href = '/login';
        }
      }
    }
    return Promise.reject(error);
  }
);

export default api; 