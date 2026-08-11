import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../features/auth/AuthContext';

export default function Header() {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <header className="border-b bg-white sticky top-0 z-10">
      <div className="max-w-6xl mx-auto px-8 py-4 flex items-center justify-between">
        <Link to="/" className="text-xl font-bold">
          🏨 Hotel Management
        </Link>

        <nav className="flex items-center gap-6">
          <Link to="/" className="text-sm hover:text-blue-600">
            Trang chủ
          </Link>

          {isAuthenticated ? (
            <>
              <Link to="/my-bookings" className="text-sm hover:text-blue-600">
                Booking của tôi
              </Link>
              <span className="text-sm text-gray-500">Xin chào, {user?.fullName}</span>
              <button
                onClick={handleLogout}
                className="text-sm bg-gray-100 px-4 py-2 rounded hover:bg-gray-200"
              >
                Đăng xuất
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="text-sm hover:text-blue-600">
                Đăng nhập
              </Link>
              <Link
                to="/register"
                className="text-sm bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
              >
                Đăng ký
              </Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}