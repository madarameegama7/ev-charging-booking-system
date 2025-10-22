import { useState } from "react";
import { Users, MapPin, Calendar, Zap, Bell, Settings, House } from "lucide-react";
import UsersOverview from "../../components/admin/UsersOverview";
import StationsOverview from "../../components/admin/StationsOverview";
import BookingsSummary from "../../components/admin/BookingsSummery";
import AdminSidebar from "../../components/admin/adminSidebar";

export default function AdminDashboard() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const [activeTab, setActiveTab] = useState("users");

  const toggleSidebar = () => setIsSidebarOpen(!isSidebarOpen);

  const sidebarItems = [
    { id: "home", label: "Home", icon: House },
    { id: "users", label: "Users Overview", icon: Users },
    { id: "stations", label: "Stations Overview", icon: MapPin },
    { id: "bookings", label: "Bookings Summary", icon: Calendar },
  ];

  const renderContent = () => {
    switch (activeTab) {
      case "users":
        return <UsersOverview />;
      case "stations":
        return <StationsOverview />;
      case "bookings":
        return <BookingsSummary />;
      default:
        return <UsersOverview />;
    }
  };

  return (
    <div className="flex h-screen bg-gray-100">
      <AdminSidebar
        isSidebarOpen={isSidebarOpen}
        toggleSidebar={toggleSidebar}
        sidebarItems={sidebarItems}
        activeTab={activeTab}
        setActiveTab={setActiveTab}
      />

      {/* Main Content */}
      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Top Bar */}
        <header className="bg-white shadow-sm border-b border-gray-200 p-6 flex justify-between items-center">
          <h2 className="text-2xl font-bold text-gray-800 capitalize">
            {sidebarItems.find((i) => i.id === activeTab)?.label}
          </h2>
          <div className="flex items-center gap-4">
            <Bell className="text-gray-600 cursor-pointer" size={24} />
            <button className="px-4 py-2 bg-gray-800 text-white rounded-lg hover:bg-black cursor-pointer">
              Logout
            </button>
          </div>
        </header>

        <main className="flex-1 overflow-y-auto p-6">{renderContent()}</main>
      </div>
    </div>
  );
}