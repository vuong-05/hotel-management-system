import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getMyBookingsApi, payBookingApi } from '../api/booking.api';

const statusLabel: Record<string, { text: string; color: string }> = {
  PENDING: { text: 'Chờ thanh toán', color: 'bg-yellow-100 text-yellow-700' },
  CONFIRMED: { text: 'Đã xác nhận', color: 'bg-green-100 text-green-700' },
  CHECKED_IN: { text: 'Đã check-in', color: 'bg-blue-100 text-blue-700' },
  CHECKED_OUT: { text: 'Đã check-out', color: 'bg-gray-100 text-gray-700' },
  CANCELLED: { text: 'Đã huỷ', color: 'bg-red-100 text-red-700' },
  NO_SHOW: { text: 'Không đến', color: 'bg-red-100 text-red-700' },
};

export default function MyBookingsPage() {
  const queryClient = useQueryClient();
  const [payingId, setPayingId] = useState<string | null>(null);
  const [message, setMessage] = useState('');

  const { data, isLoading, isError } = useQuery({
    queryKey: ['myBookings'],
    queryFn: () => getMyBookingsApi(0, 20),
  });

  const payMutation = useMutation({
    mutationFn: (bookingId: string) => payBookingApi(bookingId),
    onSuccess: () => {
      setMessage('Thanh toán thành công!');
      queryClient.invalidateQueries({ queryKey: ['myBookings'] });
    },
    onError: (err: any) => {
      setMessage(err.response?.data?.message || 'Thanh toán thất bại');
    },
    onSettled: () => setPayingId(null),
  });

  const handlePay = (bookingId: string) => {
    setPayingId(bookingId);
    setMessage('');
    payMutation.mutate(bookingId);
  };

  if (isLoading) return <div className="p-8">Đang tải danh sách booking...</div>;
  if (isError) return <div className="p-8 text-red-600">Không thể tải danh sách booking</div>;

  const bookings = data?.content || [];

  return (
    <div className="max-w-4xl mx-auto p-8">
      <h1 className="text-3xl font-bold mb-6">Booking của tôi</h1>

      {message && (
        <div className="bg-blue-100 text-blue-700 p-3 rounded mb-4">{message}</div>
      )}

      {bookings.length === 0 ? (
        <p className="text-gray-500">Bạn chưa có booking nào</p>
      ) : (
        <div className="space-y-4">
          {bookings.map((booking) => {
            const status = statusLabel[booking.status] || { text: booking.status, color: 'bg-gray-100' };
            return (
              <div key={booking.id} className="border rounded-lg p-5 shadow-sm">
                <div className="flex justify-between items-start">
                  <div>
                    <p className="font-semibold">
                      Phòng: {booking.roomNumbers.join(', ')}
                    </p>
                    <p className="text-sm text-gray-500 mt-1">
                      {booking.checkInDate} → {booking.checkOutDate} · {booking.totalGuests} khách
                    </p>
                    <p className="text-blue-600 font-bold mt-2">
                      {booking.totalAmount.toLocaleString('vi-VN')}đ
                    </p>
                  </div>
                  <span className={`text-xs px-3 py-1 rounded-full font-medium ${status.color}`}>
                    {status.text}
                  </span>
                </div>

                {booking.status === 'PENDING' && (
                  <button
                    onClick={() => handlePay(booking.id)}
                    disabled={payingId === booking.id}
                    className="mt-4 bg-green-600 text-white px-5 py-2 rounded hover:bg-green-700 disabled:opacity-50"
                  >
                    {payingId === booking.id ? 'Đang thanh toán...' : 'Thanh toán ngay'}
                  </button>
                )}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}