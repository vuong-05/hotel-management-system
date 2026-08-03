import { Routes, Route } from 'react-router-dom';
import LoginPage from '../features/auth/components/LoginPage';
import RegisterPage from '../features/auth/components/RegisterPage';
import RoomTypeList from '../features/rooms/components/RoomTypeList';

const HomePage = () => (
  <div>
    <div className="p-8 pb-0">
      <h1 className="text-3xl font-bold">Hotel Management System</h1>
      <p className="text-gray-500 mt-2">Khám phá các loại phòng của chúng tôi</p>
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
    </Routes>
  );
}