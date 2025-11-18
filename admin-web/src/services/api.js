import axios from 'axios';
import config from '../config';
import { getToken, logout } from '../utils/auth';

const api = axios.create({
  baseURL: config.apiBaseUrl,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터
api.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      logout();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// 인증 API
export const authAPI = {
  login: (username, password) =>
    api.post('/auth/login', { username, password }),
};

// 출석 API
export const attendanceAPI = {
  getAll: () => api.get('/attendance/all'),
  getByService: (serviceId) => api.get(`/attendance/service/${serviceId}`),
};

// 예배 API
export const serviceAPI = {
  getAll: () => api.get('/services/all'),
  getActive: () => api.get('/services'),
};

export default api;

