import React from 'react';

export default function StationsOverview() {
  return (
    <div className="w-full">
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 w-full">
        <div className="bg-white rounded-xl shadow-lg p-6 border-l-4 border-yellow-500">
          <h3 className="text-lg font-semibold text-gray-800">
            Total Stations
          </h3>
          <p className="text-3xl font-bold text-yellow-600 mt-2">45</p>
        </div>
        <div className="bg-white rounded-xl shadow-lg p-6 border-l-4 border-green-500">
          <h3 className="text-lg font-semibold text-gray-800">
            Active Stations
          </h3>
          <p className="text-3xl font-bold text-green-600 mt-2">42</p>
        </div>
        <div className="bg-white rounded-xl shadow-lg p-6 border-l-4 border-red-500">
          <h3 className="text-lg font-semibold text-gray-800">
            Maintenance
          </h3>
          <p className="text-3xl font-bold text-red-600 mt-2">3</p>
        </div>
      </div>
    </div>
  );
}