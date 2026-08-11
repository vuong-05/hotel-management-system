import apiClient from '../../../lib/axios';
import type { ApiResponse, PageResponse } from '../../../types/api';
import type { RoomType } from '../types/room.types';

export const getRoomTypesApi = async (page: number = 0, size: number = 10) => {
  const res = await apiClient.get<ApiResponse<PageResponse<RoomType>>>('/room-types', {
    params: { page, size },
  });
  return res.data.data;
};

export interface RoomTypePayload {
  name: string;
  description: string;
  basePrice: number;
  maxOccupancy: number;
}

export const createRoomTypeApi = async (payload: RoomTypePayload) => {
  const res = await apiClient.post<ApiResponse<RoomType>>('/room-types', payload);
  return res.data.data;
};

export const deleteRoomTypeApi = async (id: number) => {
  const res = await apiClient.delete<ApiResponse<null>>(`/room-types/${id}`);
  return res.data;
};