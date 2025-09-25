import React from "react";
import { Users, UserPlus, BarChart3, Activity } from "lucide-react";

export default function UsersOverview() {
  // Extract table data
  const users = [
    {
      nic: "199845678V",
      name: "Geethmani M.",
      email: "admin@gmail.com",
      role: "Backoffice",
      status: "Active",
      statusColor: "text-green-600"
    },
    {
      nic: "200012345V",
      name: "Nuwan P.",
      email: "nuwan@gmail.com",
      role: "EV Owner",
      status: "Inactive",
      statusColor: "text-red-600"
    }
  ];

  const columns = ["UserID", "Full Name", "Email", "Role", "Status", "Actions"];

  return (
    <div className="flex flex-col gap-12">
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {/* EV Owners */}
        <div className="bg-white rounded-xl shadow-lg p-6 border-l-4 border-blue-500">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-lg font-semibold text-gray-800">
                Total EV Owners
              </h3>
              <p className="text-3xl font-bold text-blue-600 mt-2">1,234</p>
            </div>
            <Users className="text-blue-500" size={40} />
          </div>
        </div>

        {/* Total Operators */}
        <div className="bg-white rounded-xl shadow-lg p-6 border-l-4 border-green-500">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-lg font-semibold text-gray-800">
                Total Operators
              </h3>
              <p className="text-3xl font-bold text-green-600 mt-2">89</p>
            </div>
            <UserPlus className="text-green-500" size={40} />
          </div>
        </div>

        {/*Active Users*/}
        <div className="bg-white rounded-xl shadow-lg p-6 border-l-4 border-purple-500">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-lg font-semibold text-gray-800">
                Active Users
              </h3>
              <p className="text-3xl font-bold text-purple-600 mt-2">956</p>
            </div>
            <Users className="text-purple-500" size={40} />
          </div>
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-lg p-6">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-2xl font-bold text-gray-800">User Management</h3>
          <button className="bg-blue-500 hover:bg-blue-600 text-white px-6 py-3 rounded-lg font-medium transition-colors flex items-center cursor-pointer">
            <UserPlus size={20} className="mr-2" />
            Create User
          </button>
        </div>

        {/* Clean Table Implementation */}
        <div className="overflow-x-auto">
            <table className="min-w-full bg-white rounded-lg shadow-md">
              <thead>
                <tr className="bg-gray-100 text-left">
                  {columns.map((column) => (
                    <th key={column} className="p-3 font-semibold text-gray-500">
                      {column}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {users.map((user, index) => (
                  <tr key={user.nic} className="border-b hover:bg-gray-50 transition-colors">
                    <td className="p-3">{user.nic}</td>
                    <td className="p-3">{user.name}</td>
                    <td className="p-3">{user.email}</td>
                    <td className="p-3">{user.role}</td>
                    <td className={`p-3 font-medium ${user.statusColor}`}>
                      {user.status}
                    </td>
                    <td className="p-3">
                      <button className="px-3 py-1 bg-blue-500 hover:bg-blue-600 text-white rounded transition-colors cursor-pointer">
                        Edit
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
        </div>
      </div>
    </div>
  );
}