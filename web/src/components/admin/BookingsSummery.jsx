import React, { useEffect, useState } from "react";
import { Calendar, Clock, AlertCircle } from "lucide-react";
import {
  listAllBookings,
  updateBooking,
  cancelBooking,
} from "../../api/bookings";

export default function BookingsSummary() {
  const [bookings, setBookings] = useState([]);

  useEffect(() => {
    (async () => {
      try {
        const data = await listAllBookings();
        setBookings(data);
      } catch {}
    })();
  }, []);

  const columns = [
    "Owner NIC",
    "Station",
    "Start Time",
    "End Time",
    "Status",
    "Actions",
  ];

  const getStatusColor = (status) => {
    // Normalize numeric enum values or string values to a lowercase status string
    let s = status;
    if (typeof s === "number") {
      // BookingStatus: Pending=0, Approved=1, Cancelled=2, Completed=3
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
    // capitalize
    const s = status.toString();
    return s.charAt(0).toUpperCase() + s.slice(1);
  };

  return (
    <div className="w-full space-y-6">
      {/* Bookings Management Section */}
      <div className="bg-white rounded-xl shadow-md">
        {/* Header */}
        <div className="p-6 border-b border-gray-200">
          <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
            <div>
              <h3 className="text-2xl font-bold text-gray-800">All Bookings</h3>
            </div>
          </div>
        </div>

        {/* Table */}
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
                        {b.stationId}
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
                      <div className="flex gap-2">
                        <button
                          className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-sm rounded-lg transition-all shadow-sm hover:shadow-md cursor-pointer"
                          onClick={async () => {
                            try {
                              const start = new Date(b.startTimeUtc);
                              const end = new Date(b.endTimeUtc);
                              // Example: move by +1 hour (demo update)
                              start.setHours(start.getHours() + 1);
                              end.setHours(end.getHours() + 1);
                              await updateBooking(b.id, {
                                ...b,
                                startTimeUtc: start.toISOString(),
                                endTimeUtc: end.toISOString(),
                              });
                              const data = await listAllBookings();
                              setBookings(data);
                            } catch (e) {
                              alert(e.message);
                            }
                          }}
                        >
                          Shift +1h
                        </button>
                        <button
                          className="px-4 py-2 bg-red-600 hover:bg-red-700 text-white text-sm rounded-lg transition-all shadow-sm hover:shadow-md cursor-pointer"
                          onClick={async () => {
                            if (
                              !confirm(
                                `Are you sure you want to cancel booking for ${b.ownerNIC}?`
                              )
                            )
                              return;
                            try {
                              await cancelBooking(b.id);
                              const data = await listAllBookings();
                              setBookings(data);
                            } catch (e) {
                              alert(e.message);
                            }
                          }}
                        >
                          Cancel
                        </button>
                      </div>
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

        {/* Footer */}
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
