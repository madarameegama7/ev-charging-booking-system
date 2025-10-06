import { updateStation } from "../../api/stations";

export default function StationComponent({
  stations,
  stationId,
  setStationId,
  refreshStations,
}) {
  const currentStation = stations.find((s) => s.id === stationId);

  const changeSlots = async (delta) => {
    if (!currentStation) return;
    const nextSlots = Math.max(0, currentStation.availableSlots + delta);
    await updateStation(currentStation.id, {
      ...currentStation,
      availableSlots: nextSlots,
    });
    await refreshStations();
  };

  return (
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
            onClick={() => changeSlots(+1)}
          >
            + Slot
          </button>
          <button
            className="px-3 py-1 bg-blue-600 text-white rounded"
            onClick={() => changeSlots(-1)}
          >
            - Slot
          </button>
        </div>
      )}
    </div>
  );
}
