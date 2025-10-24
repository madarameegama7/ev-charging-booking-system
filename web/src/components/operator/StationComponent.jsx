import { useState } from "react";
import { updateStation, setStationActive } from "../../api/stations";
import { 
  MapPin, 
  Zap, 
  Users, 
  Calendar, 
  Battery, 
  Settings,
  Plus,
  Minus,
  ArrowRight,
  Power,
  AlertTriangle
} from "lucide-react";

export default function StationComponent({
  stations,
  stationId,
  setStationId,
  refreshStations,
  bookings = [],
}) {
  const [isEditing, setIsEditing] = useState(false);
  const [isChangingStatus, setIsChangingStatus] = useState(false);
  const currentStation = stations.find((s) => s.id === stationId);

  const changeSlots = async (delta) => {
    if (!currentStation) return;
    const nextSlots = Math.max(0, (currentStation.availableSlots ?? 0) + delta);
    await updateStation(currentStation.id, {
      ...currentStation,
      availableSlots: nextSlots,
    });
    await refreshStations();
  };

  const computeCapacity = () => {
    if (!currentStation) return null;
    const available = Number(currentStation.availableSlots ?? 0);
    const activeBookings = (bookings || []).filter(b => {
      const status = b.status ?? b.Status ?? 0;
      return Number(status) === 0 || Number(status) === 1;
    }).length;
    const total = Number(currentStation.totalSlots ?? currentStation.capacity ?? (available + activeBookings));
    const used = Math.max(0, total - available);
    const pct = total > 0 ? Math.round((used / total) * 100) : 0;
    return { total, available, used, pct, activeBookings };
  };

  const capacity = computeCapacity();

  const nextBooking = (bookings || []).map(raw => {
    const start = raw.startTimeUtc ?? raw.start ?? raw.StartTimeUtc;
    return { raw, start: start ? new Date(start) : null };
  }).filter(b => b.start && b.start > new Date()).sort((a,b) => a.start - b.start)[0];

  const operatorDisplay = (() => {
    if (!currentStation) return null;
    if (currentStation.operatorName) return currentStation.operatorName;
    const nic = currentStation.operatorNic || currentStation.operatorNIC || '';
    if (!nic) return null;
    const first = localStorage.getItem('firstName');
    const last = localStorage.getItem('lastName');
    if (first || last) return `${first || ''} ${last || ''}`.trim() || nic;
    return nic;
  })();

  const getStatusColor = (pct) => {
    if (pct < 50) return "text-green-500";
    if (pct < 80) return "text-yellow-500";
    return "text-red-500";
  };

  const getCapacityColor = (pct) => {
    if (pct < 50) return "bg-green-500";
    if (pct < 80) return "bg-yellow-500";
    return "bg-red-500";
  };

  // Check if station can be deactivated (no active bookings)
  const canDeactivateStation = () => {
    if (!currentStation) return false;
    
    const activeBookingsCount = (bookings || []).filter(b => {
      const status = b.status ?? b.Status ?? 0;
      return Number(status) === 0 || Number(status) === 1; // Pending or Approved
    }).length;
    
    return activeBookingsCount === 0;
  };

  const handleToggleStationStatus = async () => {
    if (!currentStation) return;

    const newStatus = !currentStation.isActive;
    
    if (!newStatus) { // If deactivating
      if (!canDeactivateStation()) {
        alert("Cannot deactivate station. There are active bookings (pending or approved). Please cancel or complete all bookings first.");
        return;
      }
      
      const confirmDeactivate = window.confirm(
        "Are you sure you want to deactivate this station? " +
        "This will prevent new bookings until you reactivate it."
      );
      
      if (!confirmDeactivate) return;
    }

    setIsChangingStatus(true);
    try {
      await setStationActive(currentStation.id, newStatus);
      await refreshStations();
      
      if (newStatus) {
        alert("Station activated successfully!");
      } else {
        alert("Station deactivated successfully!");
      }
    } catch (error) {
      if (error.message === "Cannot deactivate with active bookings") {
        alert("Cannot deactivate station. There are active bookings that need to be handled first.");
      } else {
        alert(error.message || "Failed to change station status");
      }
    } finally {
      setIsChangingStatus(false);
    }
  };

  if (!currentStation) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <Battery className="mx-auto h-12 w-12 text-gray-400 mb-4" />
          <h3 className="text-lg font-medium text-gray-900">No Station Selected</h3>
          <p className="text-gray-500">Please select a station to view details</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header Section */}
      <div className="bg-gradient-to-r from-green-600 to-green-800 rounded-2xl p-6 text-white">
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <div className="flex items-center gap-3 mb-2">
              <div className="bg-white/20 p-2 rounded-lg">
                <Zap className="h-6 w-6" />
              </div>
              <div>
                <h1 className="text-2xl font-bold">{currentStation.name}</h1>
                <p className="text-green-100 opacity-90">{currentStation.type} Charging Station</p>
              </div>
            </div>
            
            <div className="flex items-center gap-4 mt-4 text-sm">
              <div className="flex items-center gap-1">
                <Users className="h-4 w-4" />
                <span>Operator: {operatorDisplay || 'Not assigned'}</span>
              </div>
              <div className={`flex items-center gap-1 ${currentStation.isActive ? 'text-green-300' : 'text-red-300'}`}>
                <div className={`w-2 h-2 rounded-full ${currentStation.isActive ? 'bg-green-300' : 'bg-red-300'}`} />
                <span>{currentStation.isActive ? 'Active' : 'Inactive'}</span>
              </div>
            </div>
          </div>
          
          <button
            onClick={() => setIsEditing(!isEditing)}
            className="bg-white/20 hover:bg-white/30 p-2 rounded-lg transition-colors"
          >
            <Settings className="h-5 w-5" />
          </button>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Capacity Card */}
        <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-gray-900">Capacity</h3>
            <Battery className="h-5 w-5 text-gray-400" />
          </div>
          <div className="space-y-3">
            <div className="flex justify-between items-center">
              <span className="text-sm text-gray-600">Utilization</span>
              <span className={`text-sm font-semibold ${getStatusColor(capacity?.pct || 0)}`}>
                {capacity?.pct || 0}%
              </span>
            </div>
            <div className="h-3 bg-gray-200 rounded-full overflow-hidden">
              <div 
                className={`h-3 rounded-full ${getCapacityColor(capacity?.pct || 0)} transition-all duration-500`}
                style={{ width: `${capacity?.pct || 0}%` }}
              />
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-gray-600">{capacity?.used || 0} used</span>
              <span className="text-gray-600">{capacity?.available || 0} available</span>
            </div>
          </div>
        </div>

        {/* Quick Actions Card */}
        <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h3>
          <div className="space-y-3">
            <div className="flex gap-2">
              <button
                onClick={() => changeSlots(+1)}
                className="flex-1 bg-green-50 hover:bg-green-100 text-green-700 border border-green-200 rounded-lg p-3 flex items-center justify-center gap-2 transition-colors"
              >
                <Plus className="h-4 w-4" />
                Add Slot
              </button>
              <button
                onClick={() => changeSlots(-1)}
                className="flex-1 bg-red-50 hover:bg-red-100 text-red-700 border border-red-200 rounded-lg p-3 flex items-center justify-center gap-2 transition-colors"
              >
                <Minus className="h-4 w-4" />
                Remove Slot
              </button>
            </div>
            
            {/* Station Status Toggle */}
            <button
              onClick={handleToggleStationStatus}
              disabled={isChangingStatus || (!currentStation.isActive && !canDeactivateStation())}
              className={`w-full flex items-center justify-center gap-2 rounded-lg p-3 transition-colors ${
                currentStation.isActive
                  ? 'bg-red-50 hover:bg-red-100 text-red-700 border border-red-200'
                  : 'bg-green-50 hover:bg-green-100 text-green-700 border border-green-200'
              } ${(isChangingStatus || (!currentStation.isActive && !canDeactivateStation())) ? 'opacity-50 cursor-not-allowed' : ''}`}
            >
              {isChangingStatus ? (
                <>
                  <div className="h-4 w-4 border-2 border-current border-t-transparent rounded-full animate-spin" />
                  {currentStation.isActive ? 'Deactivating...' : 'Activating...'}
                </>
              ) : (
                <>
                  <Power className="h-4 w-4" />
                  {currentStation.isActive ? 'Deactivate Station' : 'Activate Station'}
                </>
              )}
            </button>
          </div>
        </div>

        {/* Next Booking Card */}
        <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-gray-900">Next Booking</h3>
            <Calendar className="h-5 w-5 text-gray-400" />
          </div>
          {nextBooking ? (
            <div className="space-y-3">
              <div className="bg-blue-50 rounded-lg p-4">
                <div className="text-sm text-blue-600 font-medium">
                  {nextBooking.start.toLocaleDateString()}
                </div>
                <div className="text-lg font-semibold text-gray-900">
                  {nextBooking.start.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                </div>
              </div>
              <button className="w-full bg-blue-600 hover:bg-blue-700 text-white rounded-lg p-2 flex items-center justify-center gap-2 transition-colors">
                View Details
                <ArrowRight className="h-4 w-4" />
              </button>
            </div>
          ) : (
            <div className="text-center py-6">
              <Calendar className="h-8 w-8 text-gray-300 mx-auto mb-2" />
              <p className="text-gray-500 text-sm">No upcoming bookings</p>
            </div>
          )}
        </div>
      </div>

      {/* Station Status Alert */}
      {!currentStation.isActive && (
        <div className="bg-yellow-50 border border-yellow-200 rounded-xl p-4">
          <div className="flex items-center gap-3">
            <AlertTriangle className="h-5 w-5 text-yellow-600" />
            <div>
              <h4 className="font-medium text-yellow-800">Station Inactive</h4>
              <p className="text-yellow-700 text-sm">
                This station is currently inactive. No new bookings can be made until you activate it.
              </p>
            </div>
          </div>
        </div>
      )}

      {/* Active Bookings Warning */}
      {currentStation.isActive && !canDeactivateStation() && (
        <div className="bg-orange-50 border border-orange-200 rounded-xl p-4">
          <div className="flex items-center gap-3">
            <AlertTriangle className="h-5 w-5 text-orange-600" />
            <div>
              <h4 className="font-medium text-orange-800">Active Bookings Present</h4>
              <p className="text-orange-700 text-sm">
                This station has {capacity?.activeBookings || 0} active booking(s). 
                You need to cancel or complete all bookings before deactivating the station.
              </p>
            </div>
          </div>
        </div>
      )}

      {/* Station Selector */}
      <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">My Stations</h3>
        
        {stations.length > 0 ? (
          <>
            <select
              className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent mb-3"
              value={stationId}
              onChange={(e) => {
                setStationId(e.target.value);
                localStorage.setItem("operatorStationId", e.target.value);
              }}
            >
              <option value="">Select a station to manage...</option>
              {stations.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.name} • {s.type} • {s.availableSlots} slots • {s.isActive ? 'Active' : 'Inactive'}
                </option>
              ))}
            </select>
            
            <div className="text-sm text-gray-500">
              You have access to {stations.length} station{stations.length !== 1 ? 's' : ''}
            </div>
          </>
        ) : (
          <div className="text-center py-4">
            <Users className="h-8 w-8 text-gray-300 mx-auto mb-2" />
            <p className="text-gray-500">No stations assigned to your account</p>
            <p className="text-sm text-gray-400 mt-1">Contact administrator to get assigned to a station</p>
          </div>
        )}
      </div>

      {/* Detailed Info Section */}
      <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Station Details</h3>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
          <div>
            <div className="text-gray-500">Station ID</div>
            <div className="font-medium">{currentStation.stationId}</div>
          </div>
          <div>
            <div className="text-gray-500">Operator NIC</div>
            <div className="font-medium">{currentStation.operatorNic || 'Not set'}</div>
          </div>
          <div>
            <div className="text-gray-500">Available Slots</div>
            <div className="font-medium text-green-600">{currentStation.availableSlots}</div>
          </div>
          <div>
            <div className="text-gray-500">Active Bookings</div>
            <div className="font-medium text-blue-600">{capacity?.activeBookings || 0}</div>
          </div>
        </div>
      </div>
    </div>
  );
}