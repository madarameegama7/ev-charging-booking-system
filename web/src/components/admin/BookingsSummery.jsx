import React from 'react';

export default function BookingsSummary() {
  return (
    <div className="w-full">
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 w-full">
        <div className="bg-white rounded-xl shadow-lg p-6 border-l-4 border-indigo-500">
          <h3 className="text-lg font-semibold text-gray-800">
            Today's Bookings
          </h3>
          <p className="text-3xl font-bold text-indigo-600 mt-2">127</p>
        </div>
        <div className="bg-white rounded-xl shadow-lg p-6 border-l-4 border-orange-500">
          <h3 className="text-lg font-semibold text-gray-800">
            This Week
          </h3>
          <p className="text-3xl font-bold text-orange-600 mt-2">892</p>
        </div>
        <div className="bg-white rounded-xl shadow-lg p-6 border-l-4 border-teal-500">
          <h3 className="text-lg font-semibold text-gray-800">
            This Month
          </h3>
          <p className="text-3xl font-bold text-teal-600 mt-2">3,456</p>
        </div>
      </div>
    </div>
  );
}