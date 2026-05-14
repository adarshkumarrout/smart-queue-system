import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
  headers: { 'Content-Type': 'application/json' }
});

// Attaches JWT to every request fresh from localStorage
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('sq_token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Handles expired/invalid token globally
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('sq_token');
      localStorage.removeItem('sq_user');
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);

export default api;