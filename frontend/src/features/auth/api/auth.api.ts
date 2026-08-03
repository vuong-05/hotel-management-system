import apiClient from '../../../lib/axios';
import type { ApiResponse } from '../../../types/api';
import type { RegisterPayload, LoginPayload, AuthResponseData } from '../types/auth.types';

export const registerApi = async (payload: RegisterPayload) => {
  const res = await apiClient.post<ApiResponse<AuthResponseData>>('/auth/register', payload);
  return res.data.data;
};

export const loginApi = async (payload: LoginPayload) => {
  const res = await apiClient.post<ApiResponse<AuthResponseData>>('/auth/login', payload);
  return res.data.data;
};

export const logoutApi = async (refreshToken: string) => {
  const res = await apiClient.post<ApiResponse<null>>('/auth/logout', { refreshToken });
  return res.data;
};

export const refreshTokenApi = async (refreshToken: string) => {
  const res = await apiClient.post<ApiResponse<AuthResponseData>>('/auth/refresh-token', { refreshToken });
  return res.data.data;
};