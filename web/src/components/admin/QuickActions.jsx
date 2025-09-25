import React from 'react';
import { MapPin, Users, Calendar } from 'lucide-react';

export default function QuickActions() {
  return (
    <div className="bg-white rounded-xl shadow-lg p-6">
      <h3 className="text-xl font-bold text-gray-800 mb-6">
        Quick Actions
      </h3>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <button className="p-4 bg-blue-50 border border-blue-200 rounded-lg hover:bg-blue-100 transition-colors">
          <MapPin className="text-blue-600 mb-2" size={24} />
          <p className="font-medium text-gray-800">Add New Station</p>
        </button>
        <button className="p-4 bg-green-50 border border-green-200 rounded-lg hover:bg-green-100 transition-colors">
          <Users className="text-green-600 mb-2" size={24} />
          <p className="font-medium text-gray-800">Manage Users</p>
        </button>
        <button className="p-4 bg-yellow-50 border border-yellow-200 rounded-lg hover:bg-yellow-100 transition-colors">
          <Calendar className="text-yellow-600 mb-2" size={24} />
          <p className="font-medium text-gray-800">View Bookings</p>
        </button>
      </div>
    </div>
  );
}