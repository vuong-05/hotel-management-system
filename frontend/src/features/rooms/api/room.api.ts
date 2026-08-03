import apiClient from '../../../lib/axios';
import type { ApiResponse, PageResponse } from '../../../types/api';
import type { RoomType } from '../types/room.types';

export const getRoomTypesApi = async (page: number = 0, size: number = 10) => {
  const res = await apiClient.get<ApiResponse<PageResponse<RoomType>>>('/room-types', {
    params: { page, size },
  });
  return res.data.data;
};