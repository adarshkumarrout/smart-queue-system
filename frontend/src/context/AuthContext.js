import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../services/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('sq_user');
    return saved ? JSON.parse(saved) : null;
  });

  const login = async (username, password) => {
    const res = await api.post('/api/auth/login', { username, password });
    const data = res.data.data;
    localStorage.setItem('sq_token', data.token);
    localStorage.setItem('sq_user', JSON.stringify({ username: data.username, role: data.role, userId: data.userId }));
    api.defaults.headers.common['Authorization'] = `Bearer ${data.token}`;
    setUser({ username: data.username, role: data.role, userId: data.userId });
    return data;
  };

  const register = async (form) => {
    const res = await api.post('/api/auth/register', form);
    const data = res.data.data;
    localStorage.setItem('sq_token', data.token);
    localStorage.setItem('sq_user', JSON.stringify({ username: data.username, role: data.role, userId: data.userId }));
    api.defaults.headers.common['Authorization'] = `Bearer ${data.token}`;
    setUser({ username: data.username, role: data.role, userId: data.userId });
    return data;
  };

  const logout = () => {
    localStorage.removeItem('sq_token');
    localStorage.removeItem('sq_user');
    delete api.defaults.headers.common['Authorization'];
    setUser(null);
  };

  useEffect(() => {
    const token = localStorage.getItem('sq_token');
    if (token) api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  }, []);

  return <AuthContext.Provider value={{ user, login, register, logout }}>{children}</AuthContext.Provider>;
}

export function useAuth() { return useContext(AuthContext); }
