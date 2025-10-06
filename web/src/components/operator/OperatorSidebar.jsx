import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Menu, X, MapPin, Calendar, Zap, User } from "lucide-react";

export default function OperatorSidebar({ activeTab, setActiveTab }) {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const navigate = useNavigate();

  const toggleSidebar = () => setIsSidebarOpen(!isSidebarOpen);

  const sidebarItems = [
    { id: "station", label: "My Station", icon: MapPin },
    { id: "bookings", label: "Bookings", icon: Calendar },
    { id: "actions", label: "Quick Actions", icon: Zap },
    { id: "profile", label: "Profile", icon: User },
  ];

  const handleItemClick = (id) => {
    if (id === "profile") {
      navigate("/profile");
    } else {
      setActiveTab(id);
    }
  };

  return (
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
                Welcome, Operator
              </p>
            </div>
            <button
              onClick={toggleSidebar}
              className="text-black p-2 rounded-lg cursor-pointer"
            >
              {isSidebarOpen ? <X size={20} /> : <Menu size={20} />}
            </button>
          </div>
        </div>

        {/* Sidebar Menu */}
        <nav className="flex-1 p-4 flex flex-col gap-4">
          {sidebarItems.map((item) => {
            const Icon = item.icon;
            return (
              <button
                key={item.id}
                onClick={() => handleItemClick(item.id)}
                className={`w-full flex items-center p-4 rounded-xl transition-all duration-200 ${
                  activeTab === item.id
                    ? "bg-white/40 text-black shadow-lg"
                    : "text-black hover:text-black"
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
      </div>
    </div>
  );
}
