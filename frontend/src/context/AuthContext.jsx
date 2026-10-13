import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../services/api';

const AuthContext = createContext();

// Pre-configured demo accounts for instantaneous testing
export const DEMO_ACCOUNTS = {
  ADMIN: { email: 'admin@vehicleservice.com', password: 'admin123', name: 'System Administrator', role: 'ADMIN' },
  SERVICE_ADVISOR: { email: 'advisor@vehicleservice.com', password: 'advisor123', name: 'Rajesh Sharma', role: 'SERVICE_ADVISOR' },
  MECHANIC: { email: 'mechanic@vehicleservice.com', password: 'mechanic123', name: 'Vikram Singh', role: 'MECHANIC' },
  CUSTOMER: { email: 'customer@vehicleservice.com', password: 'customer123', name: 'Amit Verma', role: 'CUSTOMER' },
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('user');
    return saved ? JSON.parse(saved) : null;
  });
  const [token, setToken] = useState(() => localStorage.getItem('token') || null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (token) {
      localStorage.setItem('token', token);
    } else {
      localStorage.removeItem('token');
    }
  }, [token]);

  useEffect(() => {
    if (user) {
      localStorage.setItem('user', JSON.stringify(user));
    } else {
      localStorage.removeItem('user');
    }
  }, [user]);

  const login = async (email, password) => {
    setLoading(true);
    try {
      const response = await api.post('/auth/login', { email, password });
      if (response.data && response.data.success) {
        const { token: jwtToken, user: userData } = response.data.data;
        setToken(jwtToken);
        setUser(userData);
        return { success: true, role: userData.role };
      }
      return { success: false, message: response.data.message || 'Login failed' };
    } catch (err) {
      // Offline fallback: if backend is starting or offline, authenticate against demo credentials
      const matchedDemo = Object.values(DEMO_ACCOUNTS).find(
        (acc) => acc.email.toLowerCase() === email.toLowerCase() && acc.password === password
      );
      if (matchedDemo) {
        const mockUser = {
          id: matchedDemo.role === 'ADMIN' ? 1 : matchedDemo.role === 'SERVICE_ADVISOR' ? 2 : matchedDemo.role === 'MECHANIC' ? 3 : 5,
          name: matchedDemo.name,
          email: matchedDemo.email,
          role: matchedDemo.role,
          phone: '+91 9876543210',
          active: true,
        };
        const mockToken = 'mock-jwt-token-' + matchedDemo.role;
        setToken(mockToken);
        setUser(mockUser);
        return { success: true, role: matchedDemo.role, offline: true };
      }
      const msg = err.response?.data?.message || err.message || 'Unable to connect to server';
      return { success: false, message: msg };
    } finally {
      setLoading(false);
    }
  };

  const register = async (userData) => {
    setLoading(true);
    try {
      const response = await api.post('/auth/register', userData);
      if (response.data && response.data.success) {
        const { token: jwtToken, user: newUser } = response.data.data;
        setToken(jwtToken);
        setUser(newUser);
        return { success: true, role: newUser.role };
      }
      return { success: false, message: response.data.message || 'Registration failed' };
    } catch (err) {
      const msg = err.response?.data?.message || err.message || 'Registration failed';
      return { success: false, message: msg };
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        role: user?.role,
        isAuthenticated: !!token && !!user,
        loading,
        login,
        register,
        logout,
        setUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
