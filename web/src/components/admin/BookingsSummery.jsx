import React, { useEffect, useState } from "react";
import { Calendar, Clock, AlertCircle } from "lucide-react";
import {
  listAllBookings,
  updateBooking,
  cancelBooking,
} from "../../api/bookings";
import { listStations } from "../../api/stations";

export default function BookingsSummary() {
  const [bookings, setBookings] = useState([]);
  const [stations, setStations] = useState([]);
  const [stationMap, setStationMap] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
  (async () => {
    try {
      setLoading(true);
      const [bookingsData, stationsData] = await Promise.all([
        listAllBookings(),
        listStations()
      ]);
      
      console.log("Bookings:", bookingsData);
      console.log("Stations:", stationsData);
      
      setBookings(bookingsData);
      setStations(stationsData);
      
      const map = {};
      stationsData.forEach(station => {
        map[station.stationId] = station.name || `Station ${station.stationId}`;
        console.log(`Mapping: ${station.id} -> ${station.name}`);
      });
      setStationMap(map);
      
      // Test a booking's stationId
      if (bookingsData.length > 0) {
        console.log("First booking stationId:", bookingsData[0].stationId);
        console.log("Mapped name:", map[bookingsData[0].stationId]);
      }
      
    } catch (error) {
      console.error("Failed to load data:", error);
    } finally {
      setLoading(false);
    }
  })();
}, []);

  const columns = [
    "Owner NIC",
    "Station",
    "Start Time",
    "End Time",
    "Status",
  ];

  const getStatusColor = (status) => {
    let s = status;
    if (typeof s === "number") {
      s = ["pending", "approved", "cancelled", "completed"][s] ?? String(s);
    }
    s = (s ?? "").toString().toLowerCase();

    switch (s) {
      case "approved":
      case "confirmed":
        return "bg-green-100 text-green-800";
      case "pending":
        return "bg-yellow-100 text-yellow-800";
      case "cancelled":
        return "bg-red-100 text-red-800";
      case "completed":
        return "bg-blue-100 text-blue-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  const formatStatus = (status) => {
    if (typeof status === "number") {
      return ["Pending", "Approved", "Cancelled", "Completed"][status] ?? String(status);
    }
    if (!status) return "";
    const s = status.toString();
    return s.charAt(0).toUpperCase() + s.slice(1);
  };

  // Get station name from the mapping
  const getStationName = (stationId) => {
    return stationMap[stationId] || `Station ${stationId}`;
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-lg">Loading bookings...</div>
      </div>
    );
  }

  return (
    <div className="w-full space-y-6">
      <div className="bg-white rounded-xl shadow-md">
        <div className="p-6 border-b border-gray-200">
          <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
            <div>
              <h3 className="text-2xl font-bold text-gray-800">All Bookings</h3>
            </div>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="min-w-full">
            <thead className="bg-gray-50 border-b-2 border-gray-200">
              <tr>
                {columns.map((column) => (
                  <th
                    key={column}
                    className="px-6 py-4 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider"
                  >
                    {column}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {bookings.length > 0 ? (
                bookings.map((b) => (
                  <tr key={b.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className="text-sm font-medium text-gray-900">
                        {b.ownerNIC}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className="text-sm text-gray-900">
                        {getStationName(b.stationId)}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center gap-2">
                        <Clock size={16} className="text-gray-400" />
                        <span className="text-sm text-gray-900">
                          {new Date(b.startTimeUtc).toLocaleString()}
                        </span>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center gap-2">
                        <Clock size={16} className="text-gray-400" />
                        <span className="text-sm text-gray-900">
                          {new Date(b.endTimeUtc).toLocaleString()}
                        </span>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span
                        className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium ${getStatusColor(
                          b.status
                        )}`}
                      >
                        {formatStatus(b.status)}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={6} className="px-6 py-12 text-center">
                    <div className="flex flex-col items-center justify-center text-gray-400">
                      <Calendar size={48} className="mb-4" />
                      <p className="text-lg font-medium">No bookings found</p>
                      <p className="text-sm">
                        Bookings will appear here once created
                      </p>
                    </div>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        <div className="px-6 py-4 bg-gray-50 border-t border-gray-200">
          <p className="text-sm text-gray-600">
            Showing <span className="font-medium">{bookings.length}</span>{" "}
            booking{bookings.length !== 1 ? "s" : ""}
          </p>
        </div>
      </div>
    </div>
  );
}