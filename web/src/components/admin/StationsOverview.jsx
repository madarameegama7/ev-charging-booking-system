import React, { useEffect, useState } from 'react';
import { MapPin, Plus, X } from 'lucide-react';
import { listStations, createStation, updateStation, setStationActive } from '../../api/stations';
import { listUsers } from '../../api/users';
import MapPicker from '../MapPicker';

export default function StationsOverview() {
  const [stations, setStations] = useState([]);
  const [creating, setCreating] = useState(false);
  const [operators, setOperators] = useState([]);
  const [form, setForm] = useState({ name:'', type:'AC', availableSlots:1, location: { lat: 0, lng: 0 }, operatorNic: '' });

  useEffect(()=>{
    (async()=>{
      const data = await listStations();
      setStations(data);
      // load operators so admin can assign them to stations
      try {
        const users = await listUsers();
        setOperators(users.filter(u => (u.role || u.Role) === 'Operator'));
      } catch (e) {
        console.warn('Failed to load operators', e);
      }
    })();
  },[]);

  const columns = ["Name", "Type", "Operator", "Slots", "Active"];

  return (
    <div className="w-full space-y-6">
      {/* Station Management Section */}
      <div className="bg-white rounded-xl shadow-md">
        {/* Header */}
        <div className="p-6 border-b border-gray-200">
          <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
            <div>
              <h3 className="text-2xl font-bold text-gray-800">Stations Management</h3>
            </div>
            <button
              onClick={() => setCreating(true)}
              className="bg-[#347928] hover:bg-green-800 text-white px-6 py-3 rounded-lg font-medium transition-all shadow-sm hover:shadow-md flex items-center justify-center gap-2 cursor-pointer"
            >
              <Plus size={20} />
              <span>Add Station</span>
            </button>
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
              {stations.length > 0 ? (
                stations.map((s) => (
                  <tr key={s.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className="text-sm font-medium text-gray-900">{s.name}</span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className="text-sm text-gray-900">{s.type}</span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className="text-sm text-gray-900">{s.operatorNic ?? '-'}</span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className="text-sm text-gray-900">{s.availableSlots}</span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span
                        className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium ${
                          s.isActive
                            ? "bg-green-100 text-green-800"
                            : "bg-red-100 text-red-800"
                        }`}
                      >
                        {s.isActive ? "Active" : "Inactive"}
                      </span>
                    </td>
                    
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={6} className="px-6 py-12 text-center">
                    <div className="flex flex-col items-center justify-center text-gray-400">
                      <MapPin size={48} className="mb-4" />
                      <p className="text-lg font-medium">No stations found</p>
                      <p className="text-sm">Add your first charging station</p>
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
            Showing <span className="font-medium">{stations.length}</span> station{stations.length !== 1 ? 's' : ''}
          </p>
        </div>
      </div>

      {/* Create Station Modal/Popup with Blur Background */}
      {creating && (
        <div className="fixed inset-0 backdrop-blur-sm bg-white/30 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full transform transition-all max-h-[90vh] overflow-y-auto">
            {/* Modal Header */}
            <div className="flex items-center justify-between p-6 border-b border-gray-200">
              <div className="flex items-center gap-3">
                <div className="bg-[#347928] p-2 rounded-lg">
                  <MapPin className="text-white" size={24} />
                </div>
                <h3 className="text-xl font-bold text-gray-800">
                  Add New Station
                </h3>
              </div>
              <button
                onClick={() => {
                  setCreating(false);
                  setForm({ name:'', type:'AC', availableSlots:1, location: { lat: 0, lng: 0 } });
                }}
                className="text-gray-400 hover:text-gray-600 transition-colors"
              >
                <X size={24} />
              </button>
            </div>

            {/* Modal Body */}
            <div className="p-6 space-y-4">
              {/* Station Name Input */}
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Station Name <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  className="w-full border border-gray-300 rounded-lg p-3 focus:ring-2 focus:ring-[#347928] focus:border-transparent outline-none transition-all"
                  placeholder="Enter station name"
                  value={form.name}
                  onChange={(e) => setForm({ ...form, name: e.target.value })}
                />
              </div>

              {/* Type and Slots Row */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {/* Type Select */}
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">
                    Type <span className="text-red-500">*</span>
                  </label>
                  <select
                    className="w-full border border-gray-300 rounded-lg p-3 focus:ring-2 focus:ring-[#347928] focus:border-transparent outline-none transition-all cursor-pointer"
                    value={form.type}
                    onChange={(e) => setForm({ ...form, type: e.target.value })}
                  >
                    <option value="AC">AC</option>
                    <option value="DC">DC</option>
                  </select>
                </div>

                {/* Available Slots Input */}
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">
                    Available Slots <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="number"
                    min="1"
                    className="w-full border border-gray-300 rounded-lg p-3 focus:ring-2 focus:ring-[#347928] focus:border-transparent outline-none transition-all"
                    placeholder="e.g., 4"
                    value={form.availableSlots}
                    onChange={(e) => setForm({ ...form, availableSlots: Number(e.target.value) })}
                  />
                </div>
              </div>

              {/* Map Picker */}
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Location <span className="text-red-500">*</span>
                </label>
                <div className="border border-gray-300 rounded-lg overflow-hidden">
                  <MapPicker
                    value={form.location}
                    onChange={(ll) => setForm({ ...form, location: ll })}
                    height={300}
                  />
                </div>
                <div className="text-sm text-gray-600 mt-2">
                  Selected coordinates: <span className="font-medium">{form.location.lat.toFixed(6)}, {form.location.lng.toFixed(6)}</span>
                </div>
              </div>

              {/* Assign Operator */}
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Assign Operator</label>
                <select
                  className="w-full border border-gray-300 rounded-lg p-3 focus:ring-2 focus:ring-[#347928] focus:border-transparent outline-none transition-all cursor-pointer"
                  value={form.operatorNic}
                  onChange={(e) => setForm({ ...form, operatorNic: e.target.value })}
                >
                  <option value="">Unassigned</option>
                  {operators.map((o) => (
                    <option key={(o.nic ?? o.NIC)} value={(o.nic ?? o.NIC)}>{(o.name ?? `${o.firstName || ''} ${o.lastName || ''}`.trim()) || (o.nic ?? o.NIC)}</option>
                  ))}
                </select>
              </div>
            </div>

            {/* Modal Footer */}
            <div className="flex gap-3 p-6 bg-gray-50 rounded-b-2xl">
              <button
                className="flex-1 bg-gray-200 hover:bg-gray-300 text-gray-700 px-6 py-3 rounded-lg font-medium transition-all cursor-pointer"
                onClick={() => {
                  setCreating(false);
                  setForm({ name:'', type:'AC', availableSlots:1, location: { lat: 0, lng: 0 }, operatorNic: '' });
                }}
              >
                Cancel
              </button>
              <button
                className="flex-1 bg-[#347928] hover:bg-green-800 text-white px-6 py-3 rounded-lg font-medium transition-all shadow-sm hover:shadow-md cursor-pointer"
                onClick={async () => {
                  try {
                    if (!form.name.trim()) {
                      alert("Please enter a station name");
                      return;
                    }
                    if (form.availableSlots < 1) {
                      alert("Available slots must be at least 1");
                      return;
                    }
                    await createStation({ ...form });
                    const data = await listStations();
                    setStations(data);
                    setCreating(false);
                    setForm({ name:'', type:'AC', availableSlots:1, location: { lat: 0, lng: 0 }, operatorNic: '' });
                  } catch (e) {
                    alert(e.message);
                  }
                }}
              >
                Add Station
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}