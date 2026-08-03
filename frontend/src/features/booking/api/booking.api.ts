import apiClient from '../../../lib/axios';
import type { ApiResponse } from '../../../types/api';
import type { Room, BookingPayload, BookingResponseData } from '../types/booking.types';

export const getAvailableRoomsApi = async (
  checkIn: string,
  checkOut: string,
  roomTypeId?: number
) => {
  const res = await apiClient.get<ApiResponse<Room[]>>('/rooms/available', {
    params: { checkIn, checkOut, roomTypeId },
  });
  return res.data.data;
};

export const createBookingApi = async (payload: BookingPayload) => {
  const res = await apiClient.post<ApiResponse<BookingResponseData>>('/bookings', payload);
  return res.data.data;
};