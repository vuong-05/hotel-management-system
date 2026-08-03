import { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import { setAccessToken as setAxiosToken } from '../../lib/axios';
import { refreshTokenApi } from './api/auth.api';
import type { UserInfo } from './types/auth.types';

interface AuthContextType {
  user: UserInfo | null;
  accessToken: string | null;
  refreshToken: string | null;
  login: (accessToken: string, refreshToken: string, user: UserInfo) => void;
  logout: () => void;
  isAuthenticated: boolean;
  isInitializing: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserInfo | null>(null);
  const [accessToken, setAccessTokenState] = useState<string | null>(null);
  const [refreshToken, setRefreshToken] = useState<string | null>(null);
  const [isInitializing, setIsInitializing] = useState(true);

  useEffect(() => {
    const restoreSession = async () => {
      const savedRefreshToken = localStorage.getItem('refreshToken');

      if (savedRefreshToken) {
        try {
          const data = await refreshTokenApi(savedRefreshToken);
          setAccessTokenState(data.accessToken);
          setAxiosToken(data.accessToken);
          setRefreshToken(data.refreshToken);
          setUser(data.user);
          localStorage.setItem('refreshToken', data.refreshToken);
          localStorage.setItem('user', JSON.stringify(data.user));
        } catch {
          localStorage.removeItem('refreshToken');
          localStorage.removeItem('user');
        }
      }

      setIsInitializing(false);
    };

    restoreSession();
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
      value={{ user, accessToken, refreshToken, login, logout, isAuthenticated: !!user, isInitializing }}
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