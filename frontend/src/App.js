import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import UserDashboard from './pages/UserDashboard';
import AdminDashboard from './pages/AdminDashboard';
import QueuePage from './pages/QueuePage';
import Navbar from './components/Navbar';

function PrivateRoute({ children, role }) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  if (role && user.role !== role) return <Navigate to="/" replace />;
  return children;
}

function AppRoutes() {
  const { user } = useAuth();
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/login"    element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Root: redirect based on role */}
        <Route path="/" element={
          <PrivateRoute>
            {user?.role === 'ADMIN'
              ? <Navigate to="/admin" replace />
              : <Navigate to="/dashboard" replace />}
          </PrivateRoute>
        } />

        {/* User dashboard — this is where LoginPage navigates after login */}
        <Route path="/dashboard" element={
          <PrivateRoute><UserDashboard /></PrivateRoute>
        } />

        <Route path="/queue/:queueId" element={
          <PrivateRoute><QueuePage /></PrivateRoute>
        } />

        <Route path="/admin" element={
          <PrivateRoute role="ADMIN"><AdminDashboard /></PrivateRoute>
        } />

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <AppRoutes />
      </BrowserRouter>
    </AuthProvider>
  );
}