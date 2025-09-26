import React, { useEffect, useState } from 'react';
import { listAllBookings, updateBooking, cancelBooking } from '../../api/bookings';

export default function BookingsSummary() {
  const [bookings, setBookings] = useState([]);
  useEffect(()=>{
    (async()=>{
      try {
        const data = await listAllBookings();
        setBookings(data);
      } catch {}
    })();
  },[]);

  return (
    <div className="w-full space-y-6">
      <h3 className="text-2xl font-bold text-gray-800">All Bookings</h3>
      <div className="bg-white rounded-xl shadow p-4">
        <table className="min-w-full">
          <thead>
            <tr className="bg-gray-100 text-left">
              <th className="p-2">Owner NIC</th>
              <th className="p-2">Station</th>
              <th className="p-2">Start</th>
              <th className="p-2">End</th>
              <th className="p-2">Status</th>
              <th className="p-2">Actions</th>
            </tr>
          </thead>
          <tbody>
            {bookings.map(b => (
              <tr key={b.id} className="border-b">
                <td className="p-2">{b.ownerNIC}</td>
                <td className="p-2">{b.stationId}</td>
                <td className="p-2">{new Date(b.startTimeUtc).toLocaleString()}</td>
                <td className="p-2">{new Date(b.endTimeUtc).toLocaleString()}</td>
                <td className="p-2">{b.status}</td>
                <td className="p-2 flex gap-2">
                  <button className="px-2 py-1 bg-blue-600 text-white rounded" onClick={async()=>{
                    try {
                      const start = new Date(b.startTimeUtc);
                      const end = new Date(b.endTimeUtc);
                      // Example: move by +1 hour (demo update)
                      start.setHours(start.getHours()+1);
                      end.setHours(end.getHours()+1);
                      await updateBooking(b.id, { ...b, startTimeUtc: start.toISOString(), endTimeUtc: end.toISOString() });
                      const data = await listAllBookings();
                      setBookings(data);
                    } catch(e) { alert(e.message); }
                  }}>Shift +1h</button>
                  <button className="px-2 py-1 bg-red-600 text-white rounded" onClick={async()=>{
                    try {
                      await cancelBooking(b.id);
                      const data = await listAllBookings();
                      setBookings(data);
                    } catch(e) { alert(e.message); }
                  }}>Cancel</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}