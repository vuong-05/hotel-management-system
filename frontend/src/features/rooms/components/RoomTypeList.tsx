import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { getRoomTypesApi } from '../api/room.api';

export default function RoomTypeList() {
  const { data, isLoading, isError } = useQuery({
    queryKey: ['roomTypes'],
    queryFn: () => getRoomTypesApi(0, 10),
  });

  if (isLoading) {
    return <div className="text-center py-12">Đang tải danh sách phòng...</div>;
  }

  if (isError) {
    return <div className="text-center py-12 text-red-600">Không thể tải danh sách phòng</div>;
  }

  const rooms = data?.content || [];

  if (rooms.length === 0) {
    return <div className="text-center py-12 text-gray-500">Chưa có loại phòng nào</div>;
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 p-8">
      {rooms.map((room) => (
        <Link
          to={`/rooms/${room.id}`}
          key={room.id}
          className="border rounded-lg overflow-hidden shadow-sm hover:shadow-md transition block"
        >
          <div className="h-48 bg-gray-200 flex items-center justify-center text-gray-400">
            {room.images.length > 0 ? (
              <img src={room.images[0]} alt={room.name} className="w-full h-full object-cover" />
            ) : (
              <span>Chưa có ảnh</span>
            )}
          </div>
          <div className="p-4">
            <h3 className="text-lg font-semibold">{room.name}</h3>
            <p className="text-gray-500 text-sm mt-1 line-clamp-2">{room.description}</p>
            <div className="flex items-center justify-between mt-3">
              <span className="text-blue-600 font-bold">
                {room.basePrice.toLocaleString('vi-VN')}đ / đêm
              </span>
              <span className="text-sm text-gray-500">Tối đa {room.maxOccupancy} khách</span>
            </div>
          </div>
        </Link>
      ))}
    </div>
  );
}