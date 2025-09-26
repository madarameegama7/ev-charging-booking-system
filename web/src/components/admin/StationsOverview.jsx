import React, { useEffect, useState } from 'react';
import { listStations, createStation, updateStation, setStationActive } from '../../api/stations';
import MapPicker from '../MapPicker';

export default function StationsOverview() {
  const [stations, setStations] = useState([]);
  const [creating, setCreating] = useState(false);
  const [form, setForm] = useState({ name:'', type:'AC', availableSlots:1, location: { lat: 0, lng: 0 } });

  useEffect(()=>{
    (async()=>{
      const data = await listStations();
      setStations(data);
    })();
  },[]);

  return (
    <div className="w-full space-y-6">
      <div className="flex items-center justify-between">
        <h3 className="text-2xl font-bold text-gray-800">Stations</h3>
        <button onClick={()=>setCreating(true)} className="bg-blue-600 text-white px-4 py-2 rounded cursor-pointer">Add Station</button>
      </div>

      {creating && (
        <div className="grid grid-cols-1 md:grid-cols-5 gap-2">
          <input className="border p-2" placeholder="Station Name" value={form.name} onChange={(e)=>setForm({...form, name:e.target.value})} />
          <select className="border p-2" value={form.type} onChange={(e)=>setForm({...form, type:e.target.value})}>
            <option>AC</option>
            <option>DC</option>
          </select>
          <input type="number" className="border p-2" placeholder="Available Slots (e.g., 4)" value={form.availableSlots} onChange={(e)=>setForm({...form, availableSlots: Number(e.target.value)})} />
          <div className="col-span-full">
            <MapPicker value={form.location} onChange={(ll)=>setForm({...form, location: ll})} height={260} />
            <div className="text-sm text-gray-600 mt-1">Selected: {form.location.lat}, {form.location.lng}</div>
          </div>
          <div className="col-span-full flex gap-2">
            <button className="bg-green-600 text-white px-3" onClick={async()=>{
              await createStation(form);
              const data = await listStations();
              setStations(data);
              setCreating(false);
              setForm({ name:'', type:'AC', availableSlots:1, location: { lat: 0, lng: 0 } });
            }}>Save</button>
            <button className="bg-gray-300 px-3" onClick={()=>setCreating(false)}>Cancel</button>
          </div>
        </div>
      )}

      <div className="bg-white rounded-xl shadow p-4">
        <table className="min-w-full">
          <thead>
            <tr className="bg-gray-100 text-left">
              <th className="p-2">Name</th>
              <th className="p-2">Type</th>
              <th className="p-2">Slots</th>
              <th className="p-2">Active</th>
              <th className="p-2">Actions</th>
            </tr>
          </thead>
          <tbody>
            {stations.map(s => (
              <tr key={s.id} className="border-b">
                <td className="p-2">{s.name}</td>
                <td className="p-2">{s.type}</td>
                <td className="p-2">{s.availableSlots}</td>
                <td className="p-2">{s.isActive ? 'Yes' : 'No'}</td>
                <td className="p-2 flex gap-2">
                  <button className="px-2 py-1 bg-blue-600 text-white rounded" onClick={async()=>{
                    const payload = { ...s, availableSlots: s.availableSlots };
                    await updateStation(s.id, payload);
                    const data = await listStations();
                    setStations(data);
                  }}>Save</button>
                  <button className="px-2 py-1 bg-red-600 text-white rounded" onClick={async()=>{
                    try {
                      await setStationActive(s.id, !s.isActive);
                      const data = await listStations();
                      setStations(data);
                    } catch(e) { alert(e.message); }
                  }}>{s.isActive ? 'Deactivate' : 'Activate'}</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}