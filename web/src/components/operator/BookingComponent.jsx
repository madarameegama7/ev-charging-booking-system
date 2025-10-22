import { updateBooking } from "../../api/bookings";

export default function BookingComponent({
  bookings,
  loading,
  stationId,
  refreshBookings,
}) {
  const statusLabels = ["Pending", "Approved", "Cancelled", "Completed"];

  const statusColor = (s) => {
    const n = typeof s === "number" ? s : parseInt(s, 10);
    switch (n) {
      case 0:
        return "bg-yellow-100 text-yellow-800";
      case 1:
        return "bg-green-100 text-green-800";
      case 2:
        return "bg-red-100 text-red-800";
      case 3:
        return "bg-blue-100 text-blue-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  const updateStatus = async (raw, b, newStatus) => {
    const id = b.id;
    if (!id) return alert("Missing booking id");

    // Confirm destructive actions
    if (newStatus === 2) {
      const ok = window.confirm("Cancel this booking? This cannot be undone.");
      if (!ok) return;
    }
    if (newStatus === 3) {
      const ok = window.confirm("Mark booking as completed?");
      if (!ok) return;
    }

    try {
      // Use original raw object as base to avoid losing fields the server expects
      const payload = { ...raw, status: newStatus, id };
      await updateBooking(id, payload);
      await refreshBookings();
    } catch (e) {
      alert(e.message || e);
    }
  };

  return (
    <div className="bg-white rounded shadow p-4">
      <div className="flex items-center justify-between mb-3">
        <h2 className="font-semibold">Upcoming / Ongoing Bookings</h2>
        <div className="flex items-center gap-3">
          {loading && <span className="text-sm text-gray-500">Loading…</span>}
          <button
            onClick={() => refreshBookings()}
            className="px-3 py-1 bg-gray-200 rounded text-sm"
          >
            Refresh
          </button>
        </div>
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
          {bookings.map((raw) => {
            // Normalize field names returned by API
            const b = {
              id: raw.id ?? (raw._id && raw._id.$oid) ?? raw._id ?? raw.Id,
              ownerNIC: raw.ownerNIC ?? raw.ownerNic ?? raw.ownerNIC,
              startTimeUtc: raw.startTimeUtc ?? raw.start ?? raw.StartTimeUtc,
              endTimeUtc: raw.endTimeUtc ?? raw.end ?? raw.EndTimeUtc,
              status: raw.status ?? raw.Status,
            };

            const statusLabel =
              typeof b.status === "number"
                ? statusLabels[b.status] ?? b.status
                : b.status;

            return (
              <tr key={b.id} className="border-b">
                <td className="p-2">{b.ownerNIC}</td>
                <td className="p-2">
                  {b.startTimeUtc ? new Date(b.startTimeUtc).toLocaleString() : "-"}
                </td>
                <td className="p-2">
                  {b.endTimeUtc ? new Date(b.endTimeUtc).toLocaleString() : "-"}
                </td>
                <td className="p-2">
                  <span className={`px-2 py-1 rounded-full text-xs ${statusColor(b.status)}`}>
                    {statusLabel}
                  </span>
                </td>
                <td className="p-2 flex gap-2">
                  {/* Approve (from Pending -> Approved) */}
                  <button
                    className={`px-2 py-1 rounded text-white ${typeof b.status === 'number' && b.status === 0 ? 'bg-green-600' : 'bg-gray-300 cursor-not-allowed'}`}
                    onClick={() => b.status === 0 && updateStatus(raw, b, 1)}
                    disabled={!(typeof b.status === 'number' && b.status === 0)}
                  >
                    Approve
                  </button>

                  {/* Complete (from Approved -> Completed) */}
                  <button
                    className={`px-2 py-1 rounded text-white ${typeof b.status === 'number' && b.status === 1 ? 'bg-blue-600' : 'bg-gray-300 cursor-not-allowed'}`}
                    onClick={() => b.status === 1 && updateStatus(raw, b, 3)}
                    disabled={!(typeof b.status === 'number' && b.status === 1)}
                  >
                    Complete
                  </button>

                  {/* Cancel (from Pending or Approved -> Cancelled) */}
                  <button
                    className={`px-2 py-1 rounded text-white ${typeof b.status === 'number' && (b.status === 0 || b.status === 1) ? 'bg-red-600' : 'bg-gray-300 cursor-not-allowed'}`}
                    onClick={() => (b.status === 0 || b.status === 1) && updateStatus(raw, b, 2)}
                    disabled={!(typeof b.status === 'number' && (b.status === 0 || b.status === 1))}
                  >
                    Cancel
                  </button>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
