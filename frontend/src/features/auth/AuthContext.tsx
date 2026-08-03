import { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import { setAccessToken as setAxiosToken } from '../../lib/axios';
import type { UserInfo } from './types/auth.types';

interface AuthContextType {
  user: UserInfo | null;
  accessToken: string | null;
  refreshToken: string | null;
  login: (accessToken: string, refreshToken: string, user: UserInfo) => void;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserInfo | null>(null);
  const [accessToken, setAccessTokenState] = useState<string | null>(null);
  const [refreshToken, setRefreshToken] = useState<string | null>(null);

  useEffect(() => {
    const savedRefreshToken = localStorage.getItem('refreshToken');
    const savedUser = localStorage.getItem('user');
    if (savedRefreshToken && savedUser) {
      setRefreshToken(savedRefreshToken);
      setUser(JSON.parse(savedUser));
    }
  }, []);

  const login = (newAccessToken: string, newRefreshToken: string, newUser: UserInfo) => {
    setAccessTokenState(newAccessToken);
    setAxiosToken(newAccessToken);
    setRefreshToken(newRefreshToken);
    setUser(newUser);
    localStorage.setItem('refreshToken', newRefreshToken);
    localStorage.setItem('user', JSON.stringify(newUser));
  };

  const logout = () => {
    setAccessTokenState(null);
    setAxiosToken(null);
    setRefreshToken(null);
    setUser(null);
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
  };

  return (
    <AuthContext.Provider
      value={{ user, accessToken, refreshToken, login, logout, isAuthenticated: !!user }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}