import { Routes, Route } from 'react-router-dom';

const HomePage = () => <div className="p-8 text-2xl font-bold">Trang chủ Hotel Management</div>;

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
    </Routes>
  );
}