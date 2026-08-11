import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getRoomTypesApi, createRoomTypeApi, deleteRoomTypeApi } from '../api/room.api';
import { useAuth } from '../../auth/AuthContext';
import { Navigate } from 'react-router-dom';

export default function AdminRoomTypesPage() {
  const { user } = useAuth();
  const queryClient = useQueryClient();

  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [basePrice, setBasePrice] = useState('');
  const [maxOccupancy, setMaxOccupancy] = useState('2');
  const [message, setMessage] = useState('');

  const { data, isLoading } = useQuery({
    queryKey: ['adminRoomTypes'],
    queryFn: () => getRoomTypesApi(0, 50),
  });

  const createMutation = useMutation({
    mutationFn: () =>
      createRoomTypeApi({
        name,
        description,
        basePrice: Number(basePrice),
        maxOccupancy: Number(maxOccupancy),
      }),
    onSuccess: () => {
      setMessage('Tạo loại phòng thành công!');
      setName('');
      setDescription('');
      setBasePrice('');
      setMaxOccupancy('2');
      queryClient.invalidateQueries({ queryKey: ['adminRoomTypes'] });
      queryClient.invalidateQueries({ queryKey: ['roomTypes'] });
    },
    onError: (err: any) => {
      setMessage(err.response?.data?.message || 'Tạo thất bại');
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => deleteRoomTypeApi(id),
    onSuccess: () => {
      setMessage('Đã xoá loại phòng');
      queryClient.invalidateQueries({ queryKey: ['adminRoomTypes'] });
      queryClient.invalidateQueries({ queryKey: ['roomTypes'] });
    },
  });

  // Chỉ ADMIN mới vào được trang này
  if (user && user.role !== 'ADMIN') {
    return <Navigate to="/" replace />;
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setMessage('');
    createMutation.mutate();
  };

  return (
    <div className="max-w-5xl mx-auto p-8">
      <h1 className="text-3xl font-bold mb-6">Quản lý loại phòng</h1>

      {message && (
        <div className="bg-blue-100 text-blue-700 p-3 rounded mb-4">{message}</div>
      )}

      <form onSubmit={handleSubmit} className="bg-gray-50 p-6 rounded-lg mb-8 grid grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium mb-1">Tên loại phòng</label>
          <input
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
            className="w-full border rounded px-3 py-2"
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Sức chứa tối đa</label>
          <input
            type="number"
            min={1}
            value={maxOccupancy}
            onChange={(e) => setMaxOccupancy(e.target.value)}
            required
            className="w-full border rounded px-3 py-2"
          />
        </div>
        <div className="col-span-2">
          <label className="block text-sm font-medium mb-1">Mô tả</label>
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            required
            className="w-full border rounded px-3 py-2"
            rows={2}
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Giá / đêm (VNĐ)</label>
          <input
            type="number"
            min={0}
            value={basePrice}
            onChange={(e) => setBasePrice(e.target.value)}
            required
            className="w-full border rounded px-3 py-2"
          />
        </div>
        <div className="flex items-end">
          <button
            type="submit"
            disabled={createMutation.isPending}
            className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
          >
            {createMutation.isPending ? 'Đang tạo...' : 'Thêm loại phòng'}
          </button>
        </div>
      </form>

      <h2 className="text-xl font-semibold mb-3">Danh sách loại phòng</h2>
      {isLoading ? (
        <p>Đang tải...</p>
      ) : (
        <div className="space-y-2">
          {data?.content.map((room) => (
            <div key={room.id} className="flex items-center justify-between border rounded p-4">
              <div>
                <p className="font-semibold">{room.name}</p>
                <p className="text-sm text-gray-500">
                  {room.basePrice.toLocaleString('vi-VN')}đ / đêm · Tối đa {room.maxOccupancy} khách
                </p>
              </div>
              <button
                onClick={() => deleteMutation.mutate(room.id)}
                className="text-red-600 text-sm hover:underline"
              >
                Xoá
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}