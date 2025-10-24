import { useEffect, useState } from "react";
import { listStations,listStationsByOperator } from "../../api/stations";
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
    { id: "profile", label: "Profile" },
  ];

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    localStorage.removeItem("nic");
    window.location.href = "/login";
  };

// Fetch stations - Only get stations owned by current operator
useEffect(() => {
  (async () => {
    const role = localStorage.getItem("role");
    const nic = localStorage.getItem("nic");
    
    try {
      if (role === "Operator" && nic) {
        // Fetch all stations and filter by operator NIC
        const allStations = await listStations();
        const operatorStations = allStations.filter(station => 
          station.operatorNic === nic
        );
        setStations(operatorStations);
        
        if (operatorStations.length > 0) {
          // Use previously selected station or default to first one
          const savedStationId = localStorage.getItem("operatorStationId");
          const validSavedStation = operatorStations.find(s => s.id === savedStationId);
          const stationToUse = validSavedStation || operatorStations[0];
          
          setStationId(stationToUse.id);
          localStorage.setItem("operatorStationId", stationToUse.id);
        }
      } else {
        // For non-operator roles, show all stations
        const allStations = await listStations();
        setStations(allStations);
        
        if (allStations.length > 0 && (!stationId || stationId === "")) {
          const firstId = allStations[0].id;
          setStationId(firstId);
          localStorage.setItem("operatorStationId", firstId);
        }
      }
    } catch (error) {
      console.error('Error fetching stations:', error);
    }
  })();
}, []);

  // Fetch bookings for selected station
  useEffect(() => {
    (async () => {
      if (!stationId) {
        console.log("No stationId selected, skipping bookings fetch");
        return;
      }
      
      setLoading(true);
      console.log("Fetching bookings for station:", stationId);
      
      try {
        let allBookings = [];
        
        // Method 1: Try with station ID directly
        try {
          allBookings = await listBookingsByStation(stationId);
          console.log("Bookings fetched by station ID:", allBookings);
        } catch (error) {
          console.warn("Failed to fetch by station ID, trying station name:", error);
          
          // Method 2: Try with station name
          const currentStation = stations.find((s) => s.id === stationId);
          if (currentStation && currentStation.name) {
            try {
              allBookings = await listBookingsByStation(currentStation.name);
              console.log("Bookings fetched by station name:", allBookings);
            } catch (nameError) {
              console.warn("Failed to fetch by station name:", nameError);
            }
          }
        }

        // Method 3: If still no bookings, try with stationId from station object
        if (Array.isArray(allBookings) && allBookings.length === 0) {
          const currentStation = stations.find((s) => s.id === stationId);
          if (currentStation && currentStation.stationId) {
            try {
              allBookings = await listBookingsByStation(currentStation.stationId);
              console.log("Bookings fetched by station.stationId:", allBookings);
            } catch (stationIdError) {
              console.warn("Failed to fetch by station.stationId:", stationIdError);
            }
          }
        }

        setBookings(Array.isArray(allBookings) ? allBookings : []);
        
      } catch (error) {
        console.error('Error fetching bookings:', error);
        setBookings([]);
      } finally {
        setLoading(false);
      }
    })();
  }, [stationId, stations]);

  const refreshBookings = async () => {
    if (!stationId) return;
    setLoading(true);
    try {
      const updatedBookings = await listBookingsByStation(stationId);
      setBookings(updatedBookings);
    } catch (error) {
      console.error('Error refreshing bookings:', error);
    } finally {
      setLoading(false);
    }
  };

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

          {activeTab === "actions"}
        </main>
      </div>
    </div>
  );
}
