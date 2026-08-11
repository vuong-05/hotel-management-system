import { Routes, Route } from 'react-router-dom';
import LoginPage from '../features/auth/components/LoginPage';
import RegisterPage from '../features/auth/components/RegisterPage';
import RoomTypeList from '../features/rooms/components/RoomTypeList';
import RoomDetailPage from '../features/booking/components/RoomDetailPage';
import MyBookingsPage from '../features/booking/components/MyBookingsPage';
import HeroBanner from '../components/layout/HeroBanner';
import AdminRoomTypesPage from '../features/rooms/components/AdminRoomTypesPage';

const HomePage = () => (
  <div>
    <HeroBanner />
    <div className="p-8 pb-0" id="rooms">
      <h2 className="text-3xl font-bold">Các loại phòng của chúng tôi</h2>
      <p className="text-gray-500 mt-2">Khám phá không gian nghỉ dưỡng phù hợp với bạn</p>
    </div>
    <RoomTypeList />
  </div>
);

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/rooms/:id" element={<RoomDetailPage />} />
      <Route path="/my-bookings" element={<MyBookingsPage />} />
      <Route path="/admin/room-types" element={<AdminRoomTypesPage />} />
    </Routes>
  );
}