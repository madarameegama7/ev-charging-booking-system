import { useState } from "react";
import { updateBooking } from "../../api/bookings";
import { 
  RefreshCw, 
  Clock, 
  CheckCircle, 
  XCircle, 
  Calendar,
  User,
  Zap,
  Filter
} from "lucide-react";

export default function BookingComponent({
  bookings,
  loading,
  stationId,
  stations,
  refreshBookings,
}) {
  const [filterStatus, setFilterStatus] = useState("all");
  const [sortBy, setSortBy] = useState("startTime");

  const statusLabels = {
    0: "Pending",
    1: "Approved", 
    2: "Cancelled",
    3: "Completed"
  };

  const statusColors = {
    0: "bg-yellow-100 text-yellow-800 border-yellow-200",
    1: "bg-green-100 text-green-800 border-green-200",
    2: "bg-red-100 text-red-800 border-red-200",
    3: "bg-blue-100 text-blue-800 border-blue-200"
  };

  const statusIcons = {
    0: Clock,
    1: CheckCircle,
    2: XCircle,
    3: CheckCircle
  };

  // Filter and sort bookings
  const processedBookings = bookings
    .map(raw => ({
      id: raw.id ?? raw._id ?? raw.Id,
      bookingId: raw.bookingId ?? raw.bookingID,
      ownerNIC: raw.ownerNIC ?? raw.ownerNic,
      startTimeUtc: raw.startTimeUtc ?? raw.start ?? raw.StartTimeUtc,
      endTimeUtc: raw.endTimeUtc ?? raw.end ?? raw.EndTimeUtc,
      status: raw.status ?? raw.Status,
      createdAtUtc: raw.createdAtUtc ?? raw.createdAt
    }))
    .filter(booking => {
      if (filterStatus === "all") return true;
      return booking.status.toString() === filterStatus;
    })
    .sort((a, b) => {
      if (sortBy === "startTime") {
        return new Date(a.startTimeUtc) - new Date(b.startTimeUtc);
      }
      return new Date(b.createdAtUtc) - new Date(a.createdAtUtc);
    });

  const currentStation = stations?.find(s => s.id === stationId);

  const updateStatus = async (booking, newStatus) => {
    if (!booking.bookingId) {
      alert("Missing booking ID");
      return;
    }

    // Confirmations for destructive actions
    if (newStatus === 2) {
      const confirmed = window.confirm("Are you sure you want to cancel this booking? This action cannot be undone.");
      if (!confirmed) return;
    }

    if (newStatus === 3) {
      const confirmed = window.confirm("Mark this booking as completed?");
      if (!confirmed) return;
    }

    try {
      await updateBooking(booking.bookingId, {
        ...booking,
        status: newStatus
      });
      await refreshBookings();
    } catch (error) {
      alert(error.message || "Failed to update booking");
    }
  };

  const getTimeStatus = (startTime, endTime) => {
    const now = new Date();
    const start = new Date(startTime);
    const end = new Date(endTime);

    if (now < start) return "upcoming";
    if (now >= start && now <= end) return "ongoing";
    return "past";
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <RefreshCw className="h-8 w-8 text-green-600 animate-spin" />
        <span className="ml-3 text-gray-600">Loading bookings...</span>
      </div>
    );
  }

  return (
    <div className="space-y-6">

      {/* Filters */}
      <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
        <div className="flex flex-col sm:flex-row gap-4 items-start sm:items-center">
          <div className="flex items-center gap-2">
            <Filter className="h-4 w-4 text-gray-500" />
            <label className="text-sm font-medium text-gray-700">Filter by status:</label>
            <select
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value)}
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-transparent"
            >
              <option value="all">All Statuses</option>
              <option value="0">Pending</option>
              <option value="1">Approved</option>
              <option value="2">Cancelled</option>
              <option value="3">Completed</option>
            </select>
          </div>

          <div className="flex items-center gap-2">
            <label className="text-sm font-medium text-gray-700">Sort by:</label>
            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-transparent"
            >
              <option value="startTime">Start Time</option>
              <option value="createdAt">Created Date</option>
            </select>
          </div>

          <div className="text-sm text-gray-500">
            Showing {processedBookings.length} of {bookings.length} bookings
          </div>
        </div>
      </div>

      {/* Bookings Grid */}
      {processedBookings.length === 0 ? (
        <div className="bg-white rounded-xl p-12 text-center">
          <Calendar className="h-16 w-16 text-gray-300 mx-auto mb-4" />
          <h3 className="text-lg font-medium text-gray-900 mb-2">No bookings found</h3>
          <p className="text-gray-500">
            {bookings.length === 0 
              ? "There are no bookings for this station yet."
              : "No bookings match your current filter."
            }
          </p>
        </div>
      ) : (
        <div className="grid gap-4">
          {processedBookings.map((booking) => {
            const StatusIcon = statusIcons[booking.status];
            const timeStatus = getTimeStatus(booking.startTimeUtc, booking.endTimeUtc);
            const startDate = new Date(booking.startTimeUtc);
            const endDate = new Date(booking.endTimeUtc);

            return (
              <div
                key={booking.bookingId}
                className="bg-white rounded-xl p-6 shadow-sm border border-gray-100 hover:shadow-md transition-shadow"
              >
                <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
                  {/* Booking Info */}
                  <div className="flex-1">
                    <div className="flex items-center gap-3 mb-3">
                      <div className={`inline-flex items-center gap-1 px-3 py-1 rounded-full text-sm font-medium border ${statusColors[booking.status]}`}>
                        <StatusIcon className="h-3 w-3" />
                        {statusLabels[booking.status]}
                      </div>
                      {timeStatus === "ongoing" && (
                        <span className="inline-flex items-center gap-1 px-2 py-1 bg-green-100 text-green-800 text-xs rounded-full">
                          <Zap className="h-3 w-3" />
                          Ongoing
                        </span>
                      )}
                      {timeStatus === "upcoming" && (
                        <span className="inline-flex items-center gap-1 px-2 py-1 bg-blue-100 text-blue-800 text-xs rounded-full">
                          <Clock className="h-3 w-3" />
                          Upcoming
                        </span>
                      )}
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
                      <div className="flex items-center gap-2">
                        <User className="h-4 w-4 text-gray-400" />
                        <span className="text-gray-600">Customer NIC:</span>
                        <span className="font-medium">{booking.ownerNIC}</span>
                      </div>
                      <div>
                        <div className="text-gray-600">Start Time</div>
                        <div className="font-medium">{startDate.toLocaleString()}</div>
                      </div>
                      <div>
                        <div className="text-gray-600">End Time</div>
                        <div className="font-medium">{endDate.toLocaleString()}</div>
                      </div>
                    </div>

                    {booking.bookingId && (
                      <div className="mt-2 text-xs text-gray-500">
                        Booking ID: {booking.bookingId}
                      </div>
                    )}
                  </div>

                  {/* Actions */}
                  <div className="flex flex-col sm:flex-row lg:flex-col gap-2">
                    {/* Approve Button - Only for Pending */}
                    {booking.status === 0 && (
                      <button
                        onClick={() => updateStatus(booking, 1)}
                        className="flex items-center justify-center gap-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors text-sm"
                      >
                        <CheckCircle className="h-4 w-4" />
                        Mark as Approved
                      </button>
                    )}

                    {/* Complete Button - Only for Approved */}
                    {booking.status === 1 && (
                      <button
                        onClick={() => updateStatus(booking, 3)}
                        className="flex items-center justify-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm"
                      >
                        <CheckCircle className="h-4 w-4" />
                        Mark as Completed
                      </button>
                    )}

                    {/* Cancel Button - For Pending or Approved */}
                    {(booking.status === 0 || booking.status === 1) && (
                      <button
                        onClick={() => updateStatus(booking, 2)}
                        className="flex items-center justify-center gap-2 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors text-sm"
                      >
                        <XCircle className="h-4 w-4" />
                        Cancel
                      </button>
                    )}

                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}