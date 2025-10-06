import { useEffect, useState } from "react";
import { listStations, updateStation } from "../../api/stations";
import { listBookingsByStation, updateBooking } from "../../api/bookings";
import { Bell } from "lucide-react";
import OperatorSidebar from "../../components/operator/OperatorSidebar";

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

  const toggleSidebar = () => setIsSidebarOpen(!isSidebarOpen);
  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    localStorage.removeItem("nic");
    window.location.href = "/login";
  };

  useEffect(() => {
    (async () => {
      const s = await listStations();
      setStations(s);
    })();
  }, []);

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

  const currentStation = stations.find((s) => s.id === stationId);

  return (
    <div className="flex h-screen bg-gray-100">
      {/* Sidebar */}
      <OperatorSidebar activeTab={activeTab} setActiveTab={setActiveTab} />

      {/* Main */}
      <div className="flex-1 flex flex-col overflow-hidden">
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
                className="px-3 py-2 bg-gray-800 text-white rounded-md hover:bg-black cursor-pointer"
              >
                Logout
              </button>
            </div>
          </div>
        </header>

        <main className="flex-1 overflow-y-auto p-6">
          {activeTab === "station" && (
            <div className="space-y-4">
              <label className="block text-sm font-medium">My Station</label>
              <div className="flex gap-2 items-center">
                <select
                  className="border p-2"
                  value={stationId}
                  onChange={(e) => {
                    setStationId(e.target.value);
                    localStorage.setItem("operatorStationId", e.target.value);
                  }}
                >
                  <option value="">Select station…</option>
                  {stations.map((s) => (
                    <option key={s.id} value={s.id}>
                      {s.name} ({s.type})
                    </option>
                  ))}
                </select>
                {currentStation && (
                  <div className="text-sm text-gray-600">
                    Slots: {currentStation.availableSlots} • Active:{" "}
                    {currentStation.isActive ? "Yes" : "No"}
                  </div>
                )}
              </div>
              {currentStation && (
                <div className="mt-3 flex gap-2">
                  <button
                    className="px-3 py-1 bg-blue-600 text-white rounded"
                    onClick={async () => {
                      await updateStation(currentStation.id, {
                        ...currentStation,
                        availableSlots: currentStation.availableSlots + 1,
                      });
                      const s = await listStations();
                      setStations(s);
                    }}
                  >
                    + Slot
                  </button>
                  <button
                    className="px-3 py-1 bg-blue-600 text-white rounded"
                    onClick={async () => {
                      const next = Math.max(
                        0,
                        currentStation.availableSlots - 1
                      );
                      await updateStation(currentStation.id, {
                        ...currentStation,
                        availableSlots: next,
                      });
                      const s = await listStations();
                      setStations(s);
                    }}
                  >
                    - Slot
                  </button>
                </div>
              )}
            </div>
          )}

          {activeTab === "bookings" && (
            <div className="bg-white rounded shadow p-4">
              <div className="flex items-center justify-between mb-3">
                <h2 className="font-semibold">Upcoming / Ongoing Bookings</h2>
                {loading && (
                  <span className="text-sm text-gray-500">Loading…</span>
                )}
              </div>
              <table className="min-w-full">
                <thead>
                  <tr className="bg-gray-100 text-left">
                    <th className="p-2">Owner NIC</th>
                    <th className="p-2">Start</th>
                    <th className="p-2">End</th>
                    <th className="p-2">Status</th>
                    <th className="p-2">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {bookings.map((b) => (
                    <tr key={b.id} className="border-b">
                      <td className="p-2">{b.ownerNIC}</td>
                      <td className="p-2">
                        {new Date(b.startTimeUtc).toLocaleString()}
                      </td>
                      <td className="p-2">
                        {new Date(b.endTimeUtc).toLocaleString()}
                      </td>
                      <td className="p-2">{b.status}</td>
                      <td className="p-2 flex gap-2">
                        <button
                          className="px-2 py-1 bg-green-600 text-white rounded"
                          onClick={async () => {
                            try {
                              await updateBooking(b.id, { ...b, status: 1 });
                              const all = await listBookingsByStation(
                                stationId
                              );
                              setBookings(all);
                            } catch (e) {
                              alert(e.message);
                            }
                          }}
                        >
                          Check-in
                        </button>
                        <button
                          className="px-2 py-1 bg-blue-600 text-white rounded"
                          onClick={async () => {
                            try {
                              await updateBooking(b.id, { ...b, status: 3 });
                              const all = await listBookingsByStation(
                                stationId
                              );
                              setBookings(all);
                            } catch (e) {
                              alert(e.message);
                            }
                          }}
                        >
                          Complete
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {activeTab === "actions" && (
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="bg-white rounded shadow p-4">
                Adjust slots in My Station.
              </div>
              <div className="bg-white rounded shadow p-4">
                Scan QR (coming soon).
              </div>
              <div className="bg-white rounded shadow p-4">
                Report issue (coming soon).
              </div>
            </div>
          )}
        </main>
      </div>
    </div>
  );
}
