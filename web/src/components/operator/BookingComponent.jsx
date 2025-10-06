import { updateBooking } from "../../api/bookings";

export default function BookingComponent({
  bookings,
  loading,
  stationId,
  refreshBookings,
}) {
  const updateStatus = async (booking, status) => {
    try {
      await updateBooking(booking.id, { ...booking, status });
      await refreshBookings();
    } catch (e) {
      alert(e.message);
    }
  };

  return (
    <div className="bg-white rounded shadow p-4">
      <div className="flex items-center justify-between mb-3">
        <h2 className="font-semibold">Upcoming / Ongoing Bookings</h2>
        {loading && <span className="text-sm text-gray-500">Loading…</span>}
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
                  onClick={() => updateStatus(b, 1)}
                >
                  Check-in
                </button>
                <button
                  className="px-2 py-1 bg-blue-600 text-white rounded"
                  onClick={() => updateStatus(b, 3)}
                >
                  Complete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
