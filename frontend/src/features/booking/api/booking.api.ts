import apiClient from '../../../lib/axios';
import type { ApiResponse, PageResponse } from '../../../types/api';
import type { Room, BookingPayload, BookingResponseData, PaymentResponseData } from '../types/booking.types';

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

export const getMyBookingsApi = async (page: number = 0, size: number = 10) => {
  const res = await apiClient.get<ApiResponse<PageResponse<BookingResponseData>>>('/bookings/my-bookings', {
    params: { page, size },
  });
  return res.data.data;
};

export const payBookingApi = async (bookingId: string, method: string = 'MOCK') => {
  const res = await apiClient.post<ApiResponse<PaymentResponseData>>('/payments', { bookingId, method });
  return res.data.data;
};