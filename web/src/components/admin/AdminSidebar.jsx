import { useState, useEffect } from "react";
import { Menu, X, User } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { getCurrentUser } from "../../api/auth";
import logo from "../../assets/common/EV.png";

export default function AdminSidebar({
  isSidebarOpen,
  toggleSidebar,
  sidebarItems,
  activeTab,
  setActiveTab,
}) {
  const [currentUser, setCurrentUser] = useState(null);
  const navigate = useNavigate();

  const fullSidebarItems = [
    ...sidebarItems,
    { id: "profile", label: "Profile", icon: User },
  ];

  const handleItemClick = (id) => {
    if (id === "profile") {
      navigate("/profile");
    } else {
      setActiveTab(id);
    }
  };

  useEffect(() => {
    const user = getCurrentUser();
    setCurrentUser(user);
  }, []);

  return (
    <div
      className={`${
        isSidebarOpen ? "w-80" : "w-20"
      } transition-all duration-300 ease-in-out shadow-2xl flex flex-col h-full relative`}
    >
      {/* Sidebar toggle button at top */}
      <button
        onClick={toggleSidebar}
        className="absolute top-4 right-4 text-black p-2 rounded-lg cursor-pointer z-10"
      >
        {isSidebarOpen ? <X size={20} /> : <Menu size={20} />}
      </button>

      <div className="flex flex-col h-full">
        {/* Header */}
        <div className="p-6 pt-12">
          <div
            className={`${
              isSidebarOpen ? "block" : "hidden"
            } transition-all duration-300`}
          >
            <div className="flex flex-col gap-10">
              <div className="flex gap-6">
                <div className="flex">
                  <img
                    src={logo}
                    alt="logo"
                    width={60}
                    height={60}
                    className="rounded-full"
                  />
                </div>
                <div className="flex items-center">
                  <h1 className="text-2xl font-bold text-[#347928]">
                    EVCharge
                  </h1>
                </div>
              </div>
              <div className="flex justify-center">
                <p className="text-gray-800 text-base">
                  Welcome {currentUser?.role || "Admin"}!
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Navigation */}
        <nav className="flex-1 p-4 flex flex-col gap-4">
          {fullSidebarItems.map((item) => {
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

        {/* Footer – Logged-in Admin Info */}
        {isSidebarOpen && currentUser && (
          <div className="flex items-center justify-center gap-3 mt-auto rounded-xl mb-6">
            <div className="w-10 h-10 flex items-center justify-center bg-[#347928] text-white font-bold rounded-full">
              {currentUser.firstName?.slice(0, 2).toUpperCase() || "NA"}
            </div>
            <div className="flex flex-col">
              <span className="font-medium text-gray-800">
                {currentUser.firstName} {currentUser.lastName}
              </span>
              <span className="text-sm text-gray-500">
                {currentUser.email || currentUser.nic}
              </span>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}