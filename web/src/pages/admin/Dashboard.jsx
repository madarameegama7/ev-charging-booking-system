import { useState } from "react";
import {
  Users,
  MapPin,
  Calendar,
  Zap,
  Bell,
  Settings,
  Menu,
  X,
} from "lucide-react";
import userimage from "../../assets/common/userprofile.jpeg";
import UsersOverview from "../../components/admin/UsersOverview";
import StationsOverview from "../../components/admin/StationsOverview";
import BookingsSummary from "../../components/admin/BookingsSummery";
import QuickActions from "../../components/admin/QuickActions";

export default function AdminDashboard() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const [activeTab, setActiveTab] = useState("users");

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('nic');
    window.location.href = '/login';
  };

  const sidebarItems = [
    { id: "users", label: "Users Overview", icon: Users },
    { id: "stations", label: "Stations Overview", icon: MapPin },
    { id: "bookings", label: "Bookings Summary", icon: Calendar },
    { id: "actions", label: "Quick Actions", icon: Zap },
    { id: "settings", label: "Settings", icon: Settings },
  ];

  const renderContent = () => {
    switch (activeTab) {
      case "users":
        return <UsersOverview />;
      case "stations":
        return <StationsOverview />;
      case "bookings":
        return <BookingsSummary />;
      case "actions":
        return <QuickActions />;
      case "settings":
        return <Settings />;
    }
  };

  return (
    <div className="flex h-screen bg-gray-100">
      {/* Sidebar */}
      <div
        className={`${
          isSidebarOpen ? "w-80" : "w-20"
        } transition-all duration-300 ease-in-out shadow-2xl`}
      >
        <div className="flex flex-col h-full">
          {/* Header */}
          <div className="p-6">
            <div className="flex items-center justify-between">
              <div
                className={`${
                  isSidebarOpen ? "block" : "hidden"
                } transition-all duration-300`}
              >
                <h1 className="text-2xl font-bold text-blue-600 flex items-center">
                  EVConnect
                </h1>
                <p className="text-black text-sm mt-1 text-center">
                  Welcome back, Admin!
                </p>
              </div>
              <button
                onClick={toggleSidebar}
                className="text-black p-2 rounded-lg transition-colors cursor-pointer"
              >
                {isSidebarOpen ? <X size={20} /> : <Menu size={20} />}
              </button>
            </div>
          </div>

          {/* Navigation */}
          <nav className="flex-1 p-4 flex flex-col gap-4">
            {sidebarItems.map((item) => {
              const Icon = item.icon;
              return (
                <button
                  key={item.id}
                  onClick={() => setActiveTab(item.id)}
                  className={`w-full flex items-center p-4 rounded-xl transition-all duration-200 group ${
                    activeTab === item.id
                      ? "bg-white/40 text-black shadow-lg"
                      : "text-black hover:text-black"
                  }`}
                >
                  <Icon size={22} className="flex-shrink-0" />
                  <span
                    className={`${
                      isSidebarOpen ? "ml-4 block" : "hidden"
                    } font-medium transition-all duration-300`}
                  >
                    {item.label}
                  </span>
                </button>
              );
            })}
          </nav>

          {/* Footer */}
          <div className={`p-4 ${isSidebarOpen ? "block" : "hidden"}`}>
            <div className="bg-white/10 rounded-lg p-4 backdrop-blur-sm">
              <div className="flex items-center">
                <div className="ml-3">
                  <div className="flex gap-4 items-center">
                    <img
                      src={userimage}
                      alt="User avatar"
                      className="w-8 h-8 rounded-full object-cover"
                    />
                    <div className="flex flex-col">
                      <p className="text-black font-medium">
                        Geethmani Mirahawaththa
                      </p>
                      <p className="text-black text-xs">admin@gmail.com</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Top Bar */}
        <header className="bg-white shadow-sm border-b border-gray-200 p-6">
          <div className="flex items-center justify-between">
            <h2 className="text-2xl font-bold text-gray-800 capitalize">
              {sidebarItems.find((item) => item.id === activeTab)?.label}
            </h2>
            <div className="flex items-center space-x-4">
              <div className="relative">
                <Bell
                  className="text-gray-600 hover:text-gray-800 cursor-pointer"
                  size={24}
                />
                <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                  3
                </span>
              </div>
              <button onClick={handleLogout} className="px-3 py-2 bg-gray-800 text-white rounded-md hover:bg-black cursor-pointer">Logout</button>
              <div className="w-10 h-10 bg-blue-500 rounded-full flex items-center justify-center">
                <span className="text-white font-bold">A</span>
              </div>
            </div>
          </div>
        </header>

        {/* Content Area */}
        <main className="flex-1 overflow-y-auto p-6">{renderContent()}</main>
      </div>
    </div>
  );
}
