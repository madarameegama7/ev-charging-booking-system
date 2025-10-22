import { updateStation } from "../../api/stations";

export default function StationComponent({
  stations,
  stationId,
  setStationId,
  refreshStations,
  bookings = [],
}) {
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

  // Calculate occupancy: if station has a configured total capacity, use it; otherwise
  // approximate by availableSlots + active bookings for the station
  const computeCapacity = () => {
    if (!currentStation) return null;
    const available = Number(currentStation.availableSlots ?? 0);
    const activeBookings = (bookings || []).filter(b => {
      const status = b.status ?? b.Status ?? 0;
      // treat 0/1 (Pending/Approved) as occupying a slot
      return Number(status) === 0 || Number(status) === 1;
    }).length;
    // If station reports a 'totalSlots' or 'capacity' field, use that
    const total = Number(currentStation.totalSlots ?? currentStation.capacity ?? (available + activeBookings));
    const used = Math.max(0, total - available);
    const pct = total > 0 ? Math.round((used / total) * 100) : 0;
    return { total, available, used, pct, activeBookings };
  };

  const capacity = computeCapacity();

  // Find next booking start time (nearest upcoming start)
  const nextBooking = (bookings || []).map(raw => {
    const start = raw.startTimeUtc ?? raw.start ?? raw.StartTimeUtc;
    return { raw, start: start ? new Date(start) : null };
  }).filter(b => b.start && b.start > new Date()).sort((a,b) => a.start - b.start)[0];

  // Resolve operator display name: try station.operatorName, then map operatorNic to stored name
  const operatorDisplay = (() => {
    if (!currentStation) return null;
    if (currentStation.operatorName) return currentStation.operatorName;
    const nic = currentStation.operatorNic || currentStation.operatorNIC || '';
    if (!nic) return null;
    // Try localStorage (login may have stored names)
    const first = localStorage.getItem('firstName');
    const last = localStorage.getItem('lastName');
    if (first || last) return `${first || ''} ${last || ''}`.trim() || nic;
    return nic;
  })();

  return (
    <div className="space-y-4">
      <div className="flex items-start justify-between">
        <div>
          <label className="block text-sm font-medium text-gray-600">My Station</label>
          <h3 className="text-xl font-bold text-gray-800">{currentStation?.name ?? '—'}</h3>
          <div className="text-sm text-gray-600 mt-1">
            {currentStation?.type ? <span className="mr-2">{currentStation.type}</span> : null}
            {currentStation && <span>• Active: {currentStation.isActive ? 'Yes' : 'No'}</span>}
            {operatorDisplay ? <span className="ml-3">• Operator: {operatorDisplay}</span> : null}
          </div>
        </div>

        <div className="text-right">
          {capacity && (
            <div className="w-48 text-sm">
              <div className="flex items-center justify-between text-xs text-gray-500 mb-1">
                <span>Capacity</span>
                <span>{capacity.used}/{capacity.total}</span>
              </div>
              <div className="h-3 bg-gray-200 rounded overflow-hidden">
                <div className="h-3 bg-green-500" style={{ width: `${capacity.pct}%` }} />
              </div>
            </div>
          )}
        </div>
      </div>

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
            Slots available: <strong>{capacity ? capacity.available : currentStation.availableSlots}</strong>
            {currentStation.operatorNic ? (
              <span className="ml-3">• Operator NIC: {currentStation.operatorNic}</span>
            ) : null}
          </div>
        )}
      </div>

      {nextBooking && (
        <div className="bg-white rounded p-3 shadow-sm">
          <div className="text-sm text-gray-500">Next booking</div>
          <div className="text-sm font-medium">{nextBooking.start.toLocaleString()}</div>
        </div>
      )}

      {currentStation && (
        <div className="mt-3 flex gap-2">
          <button
            className="px-3 py-1 bg-[#347928] text-white hover:bg-green-800 rounded-lg"
            onClick={() => changeSlots(+1)}
          >
            + Slot
          </button>
          <button
            className="px-3 py-1 bg-[#347928] text-white hover:bg-green-800 rounded-lg"
            onClick={() => changeSlots(-1)}
          >
            - Slot
          </button>
        </div>
      )}
    </div>
  );
}
