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
      // Auto-select station assigned to this operator if available
      const role = localStorage.getItem("role");
      const nic = localStorage.getItem("nic");
      if (role === "Operator" && nic) {
        // try to find station where operatorNic matches current operator NIC
        const assigned = s.find((st) => (st.operatorNic ?? st.operatorNIC ?? '') === nic);
        if (assigned) {
          setStationId(assigned.id);
          localStorage.setItem("operatorStationId", assigned.id);
          return;
        }
      }

      // If operator hasn't selected a station yet, auto-select the first available station
      if ((!stationId || stationId === "") && s && s.length > 0) {
        const firstId = s[0].id;
        setStationId(firstId);
        localStorage.setItem("operatorStationId", firstId);
      }
    })();
  }, []);

  // Fetch bookings for selected station
  useEffect(() => {
    (async () => {
      if (!stationId) return;
      setLoading(true);
      try {
        // First try using the station id (normal case)
        let all = await listBookingsByStation(stationId);

        // Fallback: some bookings were stored using station name instead of id.
        // If we got no results, try querying by the station name.
        if (Array.isArray(all) && all.length === 0) {
          const st = stations.find((s) => s.id === stationId);
          if (st && st.name) {
            const byName = await listBookingsByStation(st.name);
            all = byName;
          }
        }

        // Final tolerant fallback: fetch all bookings and filter those whose stationId
        // looks like the station name (covers legacy data where stationId stores a name)
        if (Array.isArray(all) && all.length === 0) {
          try {
            const allBookings = await import('../../api/bookings').then(m => m.listAllBookings());
            const st = stations.find((s) => s.id === stationId);
            if (st && st.name) {
              const name = st.name.toLowerCase();
              const filtered = (allBookings || []).filter(b => {
                const sid = (b.stationId ?? b.stationID ?? b.StationId ?? '') + '';
                return sid.toLowerCase() === name || sid.toLowerCase().includes(name);
              });
              all = filtered;
            }
          } catch (e) {
            console.warn('Tolerant booking fallback failed', e);
          }
        }

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
              bookings={bookings}
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
