import { useEffect, useState } from "react";
import { listStations } from "../../api/stations";
import { listBookingsByStation } from "../../api/bookings";
import { Bell } from "lucide-react";
import OperatorSidebar from "../../components/operator/OperatorSidebar";
import StationComponent from "../../components/operator/StationComponent";
import BookingComponent from "../../components/operator/BookingComponent";
import QuickActionComponent from "../../components/operator/QuickActionComponent";

export default function OperatorDashboard() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const [activeTab, setActiveTab] = useState("station");
  const [stations, setStations] = useState([]);
  const [stationId, setStationId] = useState(
    localStorage.getItem("operatorStationId") || ""
  );
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(false);

  const sidebarItems = [
    { id: "station", label: "My Station" },
    { id: "bookings", label: "Bookings" },
    { id: "actions", label: "Quick Actions" },
    { id: "profile", label: "Profile" },
  ];

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    localStorage.removeItem("nic");
    window.location.href = "/login";
  };

  // Fetch stations
  useEffect(() => {
    (async () => {
      const s = await listStations();
      setStations(s);
    })();
  }, []);

  // Fetch bookings for selected station
  useEffect(() => {
    (async () => {
      if (!stationId) return;
      setLoading(true);
      try {
        const all = await listBookingsByStation(stationId);
        setBookings(all);
      } finally {
        setLoading(false);
      }
    })();
  }, [stationId]);

  return (
    <div className="flex h-screen bg-gray-100">
      <OperatorSidebar activeTab={activeTab} setActiveTab={setActiveTab} />

      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Header */}
        <header className="bg-white shadow-sm border-b border-gray-200 p-6">
          <div className="flex items-center justify-between">
            <h2 className="text-2xl font-bold text-gray-800 capitalize">
              {sidebarItems.find((i) => i.id === activeTab)?.label}
            </h2>
            <div className="flex items-center gap-4">
              <div className="relative">
                <Bell className="text-gray-600" size={24} />
                <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                  3
                </span>
              </div>
              <button
                onClick={handleLogout}
                className="px-6 py-2 bg-[#347928] text-white rounded-lg hover:bg-green-800 cursor-pointer"
              >
                Logout
              </button>
            </div>
          </div>
        </header>

        {/* Main content area */}
        <main className="flex-1 overflow-y-auto p-6">
          {activeTab === "station" && (
            <StationComponent
              stations={stations}
              stationId={stationId}
              setStationId={setStationId}
              refreshStations={() => listStations().then(setStations)}
            />
          )}

          {activeTab === "bookings" && (
            <BookingComponent
              bookings={bookings}
              loading={loading}
              stationId={stationId}
              refreshBookings={() =>
                listBookingsByStation(stationId).then(setBookings)
              }
            />
          )}

          {activeTab === "actions" && <QuickActionComponent />}
        </main>
      </div>
    </div>
  );
}
