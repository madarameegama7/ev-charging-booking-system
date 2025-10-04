import { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import {
  Users,
  MapPin,
  Calendar,
  Zap,
  Bell,
  Settings,
  Menu,
  X,
  UserCircle,
} from "lucide-react";

export default function DashboardLayout({ children }) {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const [userData, setUserData] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const location = useLocation();
  const role = localStorage.getItem("role");
  const nic = localStorage.getItem("nic");

  const toggleSidebar = () => setIsSidebarOpen(!isSidebarOpen);

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  // Fetch user details from API
  useEffect(() => {
    const fetchUserData = async () => {
      if (!nic) {
        setLoading(false);
        return;
      }

      try {
        const token = localStorage.getItem("token");
        const response = await fetch(`/api/User/${nic}`, {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });

        if (response.ok) {
          const data = await response.json();
          setUserData(data);
        } else {
          console.error("Failed to fetch user data");
        }
      } catch (error) {
        console.error("Error fetching user data:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchUserData();
  }, [nic]);

  // Different sidebar items based on role
  const adminItems = [
    { id: "users", label: "Users Overview", icon: Users, path: "/admin/dashboard" },
    { id: "stations", label: "Stations Overview", icon: MapPin, path: "/admin/dashboard" },
    { id: "bookings", label: "Bookings Summary", icon: Calendar, path: "/admin/dashboard" },
    { id: "actions", label: "Quick Actions", icon: Zap, path: "/admin/dashboard" },
    { id: "profile", label: "Profile", icon: UserCircle, path: "/profile" },
    { id: "settings", label: "Settings", icon: Settings, path: "/admin/dashboard" },
  ];

  const operatorItems = [
    { id: "station", label: "My Station", icon: MapPin, path: "/operator/dashboard" },
    { id: "bookings", label: "Bookings", icon: Calendar, path: "/operator/dashboard" },
    { id: "actions", label: "Quick Actions", icon: Zap, path: "/operator/dashboard" },
    { id: "profile", label: "Profile", icon: UserCircle, path: "/profile" },
  ];

  const sidebarItems = role === "Backoffice" ? adminItems : operatorItems;

  const getActiveItem = () => {
    if (location.pathname === "/profile") return "profile";
    if (location.pathname.includes("/admin")) return "users";
    if (location.pathname.includes("/operator")) return "station";
    return "";
  };

  const activeItem = getActiveItem();

  return (
    <div className="flex h-screen bg-gray-100">
      {/* Sidebar */}
      <div
        className={`${
          isSidebarOpen ? "w-80" : "w-20"
        } transition-all duration-300 ease-in-out shadow-2xl bg-white`}
      >
        <div className="flex flex-col h-full">
          {/* Header */}
          <div className="p-6 border-b">
            <div className="flex items-center justify-between">
              <div
                className={`${
                  isSidebarOpen ? "block" : "hidden"
                } transition-all duration-300`}
              >
                <h1 className="text-2xl font-bold text-[#347928]">EVConnect</h1>
                <p className="text-gray-600 text-sm mt-1">
                  {role === "Backoffice" ? "Admin Panel" : "Operator Panel"}
                </p>
              </div>
              <button
                onClick={toggleSidebar}
                className="text-gray-600 hover:text-gray-800 p-2 rounded-lg transition-colors cursor-pointer"
              >
                {isSidebarOpen ? <X size={20} /> : <Menu size={20} />}
              </button>
            </div>
          </div>

          {/* Navigation */}
          <nav className="flex-1 p-4 flex flex-col gap-2">
            {sidebarItems.map((item) => {
              const Icon = item.icon;
              const isActive = activeItem === item.id;
              return (
                <button
                  key={item.id}
                  onClick={() => navigate(item.path)}
                  className={`w-full flex items-center p-4 rounded-xl transition-all duration-200 ${
                    isActive
                      ? "bg-[#347928]/10 text-[#347928] shadow-md"
                      : "text-gray-600 hover:bg-gray-100"
                  }`}
                >
                  <Icon size={22} className="flex-shrink-0" />
                  <span
                    className={`${
                      isSidebarOpen ? "ml-4 block" : "hidden"
                    } font-medium`}
                  >
                    {item.label}
                  </span>
                </button>
              );
            })}
          </nav>

          {/* Footer - User Info */}
          {isSidebarOpen && (
            <div className="p-4 border-t">
              <div className="bg-gray-50 rounded-lg p-4">
                {loading ? (
                  <div className="text-sm text-gray-500 text-center">
                    Loading...
                  </div>
                ) : userData ? (
                  <div className="space-y-3">
                    <div className="flex items-center gap-3">
                      <div className="w-12 h-12 rounded-full bg-[#347928] flex items-center justify-center flex-shrink-0">
                        <span className="text-white font-bold text-lg">
                          {userData.name?.charAt(0).toUpperCase() || "U"}
                        </span>
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-semibold text-gray-900 truncate">
                          {userData.name || "User"}
                        </p>
                        <p className="text-xs text-gray-500 truncate">
                          {userData.role}
                        </p>
                      </div>
                    </div>
                    <div className="space-y-1 pt-2 border-t border-gray-200">
                      <div className="flex items-center gap-2">
                        <span className="text-xs text-gray-500">Email:</span>
                        <span className="text-xs text-gray-700 truncate">
                          {userData.email || "N/A"}
                        </span>
                      </div>
                      <div className="flex items-center gap-2">
                        <span className="text-xs text-gray-500">Phone:</span>
                        <span className="text-xs text-gray-700">
                          {userData.phone || "N/A"}
                        </span>
                      </div>
                      <div className="flex items-center gap-2">
                        <span className="text-xs text-gray-500">NIC:</span>
                        <span className="text-xs text-gray-700">
                          {userData.nic}
                        </span>
                      </div>
                    </div>
                  </div>
                ) : (
                  <div className="text-sm text-gray-500 text-center">
                    No user data available
                  </div>
                )}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Main Content */}
      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Top Bar */}
        <header className="bg-white shadow-sm border-b border-gray-200 p-6">
          <div className="flex items-center justify-between">
            <h2 className="text-2xl font-bold text-gray-800">
              {location.pathname === "/profile"
                ? "Profile"
                : sidebarItems.find((i) => i.id === activeItem)?.label || "Dashboard"}
            </h2>
            <div className="flex items-center gap-4">
              <div className="relative">
                <Bell
                  className="text-gray-600 hover:text-gray-800 cursor-pointer"
                  size={24}
                />
                <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                  3
                </span>
              </div>
              <button
                onClick={handleLogout}
                className="px-4 py-2 bg-gray-800 text-white rounded-lg hover:bg-black cursor-pointer transition-colors"
              >
                Logout
              </button>
            </div>
          </div>
        </header>

        {/* Content Area */}
        <main className="flex-1 overflow-y-auto">{children}</main>
      </div>
    </div>
  );
}