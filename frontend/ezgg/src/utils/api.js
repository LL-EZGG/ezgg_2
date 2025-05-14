import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8888',
  timeout: 10000,
  withCredentials: true,
});

// 토큰 관련 유틸리티 함수들
const tokenUtils = {
  get: () => localStorage.getItem('token'),
  set: (token) => localStorage.setItem('token', 'Bearer ' + token),
  remove: () => localStorage.removeItem('token'),
  format: (token) => token.startsWith('Bearer ') ? token : `Bearer ${token}`,
};

// 토큰 리프레시 함수
const refreshToken = async () => {
  try {
    const { headers: { authorization: newToken } } = await axios.post(
      'http://localhost:8888/refresh',
      {},
      {
        withCredentials: true,
        headers: { 'Content-Type': 'application/json' }
      }
    );

    if (!newToken) throw new Error('새 토큰을 받지 못했습니다.');
    tokenUtils.set(newToken);
    return newToken;
  } catch (error) {
    tokenUtils.remove();
    if (!window.location.pathname.includes('/login')) {
      window.location.href = '/login';
    }
    throw error;
  }
};

// 요청 인터셉터
api.interceptors.request.use(
  (config) => {
    const token = tokenUtils.get();
    if (token) {
      config.headers['Authorization'] = tokenUtils.format(token);
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 응답 인터셉터
api.interceptors.response.use(
  (response) => {
    const token = response.headers['authorization'];
    if (token) tokenUtils.set(token);
    return response;
  },
  async (error) => {
    const isTokenError = error.response?.status === 401 || error.response?.status === 403;
    const shouldRefresh = isTokenError && !error.config._retry && error.config.url !== '/refresh';

    if (!shouldRefresh) return Promise.reject(error);

    try {
      error.config._retry = true;
      const newToken = await refreshToken();
      error.config.headers['Authorization'] = newToken;
      return api(error.config);
    } catch (refreshError) {
      return Promise.reject(refreshError);
    }
  }
);

export default api;