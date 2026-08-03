import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation } from '@tanstack/react-query';
import apiClient from '../../../lib/axios';
import type { ApiResponse } from '../../../types/api';
import type { RoomType } from '../../rooms/types/room.types';
import { getAvailableRoomsApi, createBookingApi } from '../api/booking.api';
import { useAuth } from '../../auth/AuthContext';
import type { Room } from '../types/booking.types';

export default function RoomDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  const [checkIn, setCheckIn] = useState('');
  const [checkOut, setCheckOut] = useState('');
  const [totalGuests, setTotalGuests] = useState(1);
  const [availableRooms, setAvailableRooms] = useState<Room[] | null>(null);
  const [selectedRoomId, setSelectedRoomId] = useState<number | null>(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const { data: roomType, isLoading } = useQuery({
    queryKey: ['roomType', id],
    queryFn: async () => {
      const res = await apiClient.get<ApiResponse<RoomType>>(`/room-types/${id}`);
      return res.data.data;
    },
  });

  const searchMutation = useMutation({
    mutationFn: () => getAvailableRoomsApi(checkIn, checkOut, Number(id)),
    onSuccess: (data) => {
      setAvailableRooms(data);
      setError('');
    },
    onError: () => {
      setError('Không thể tìm phòng trống, vui lòng thử lại');
    },
  });

  const bookingMutation = useMutation({
    mutationFn: () =>
      createBookingApi({
        checkInDate: checkIn,
        checkOutDate: checkOut,
        totalGuests,
        roomIds: [selectedRoomId!],
      }),
    onSuccess: (data) => {
      setSuccess(`Đặt phòng thành công! Mã booking: ${data.id}`);
      setError('');
    },
    onError: (err: any) => {
      setError(err.response?.data?.message || 'Đặt phòng thất bại');
      setSuccess('');
    },
  });

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setSuccess('');
    searchMutation.mutate();
  };

  const handleBooking = () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    if (!selectedRoomId) {
      setError('Vui lòng chọn 1 phòng trước khi đặt');
      return;
    }
    bookingMutation.mutate();
  };

  if (isLoading) return <div className="p-8">Đang tải...</div>;
  if (!roomType) return <div className="p-8">Không tìm thấy loại phòng</div>;

  return (
    <div className="max-w-4xl mx-auto p-8">
      <h1 className="text-3xl font-bold">{roomType.name}</h1>
      <p className="text-gray-600 mt-2">{roomType.description}</p>
      <p className="text-blue-600 font-bold text-xl mt-2">
        {roomType.basePrice.toLocaleString('vi-VN')}đ / đêm
      </p>
      <p className="text-gray-500">Tối đa {roomType.maxOccupancy} khách</p>

      <form onSubmit={handleSearch} className="bg-gray-50 p-6 rounded-lg mt-6 flex gap-4 items-end flex-wrap">
        <div>
          <label className="block text-sm font-medium mb-1">Check-in</label>
          <input
            type="date"
            value={checkIn}
            onChange={(e) => setCheckIn(e.target.value)}
            required
            className="border rounded px-3 py-2"
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Check-out</label>
          <input
            type="date"
            value={checkOut}
            onChange={(e) => setCheckOut(e.target.value)}
            required
            className="border rounded px-3 py-2"
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Số khách</label>
          <input
            type="number"
            min={1}
            value={totalGuests}
            onChange={(e) => setTotalGuests(Number(e.target.value))}
            className="border rounded px-3 py-2 w-20"
          />
        </div>
        <button
          type="submit"
          disabled={searchMutation.isPending}
          className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700"
        >
          {searchMutation.isPending ? 'Đang tìm...' : 'Tìm phòng trống'}
        </button>
      </form>

      {error && <div className="bg-red-100 text-red-700 p-3 rounded mt-4">{error}</div>}
      {success && <div className="bg-green-100 text-green-700 p-3 rounded mt-4">{success}</div>}

      {availableRooms && (
        <div className="mt-6">
          <h2 className="text-xl font-semibold mb-3">Phòng còn trống</h2>
          {availableRooms.length === 0 ? (
            <p className="text-gray-500">Không còn phòng trống trong khoảng ngày này</p>
          ) : (
            <div className="space-y-2">
              {availableRooms.map((room) => (
                <label
                  key={room.id}
                  className={`flex items-center gap-3 border rounded p-3 cursor-pointer ${
                    selectedRoomId === room.id ? 'border-blue-600 bg-blue-50' : ''
                  }`}
                >
                  <input
                    type="radio"
                    name="room"
                    checked={selectedRoomId === room.id}
                    onChange={() => setSelectedRoomId(room.id)}
                  />
                  <span>Phòng {room.roomNumber} — Tầng {room.floor}</span>
                </label>
              ))}
              <button
                onClick={handleBooking}
                disabled={bookingMutation.isPending}
                className="bg-green-600 text-white px-6 py-2 rounded hover:bg-green-700 mt-3"
              >
                {bookingMutation.isPending ? 'Đang đặt...' : 'Đặt phòng ngay'}
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}