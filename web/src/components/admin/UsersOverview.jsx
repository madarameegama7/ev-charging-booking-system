import React, { useEffect, useState } from "react";
import {
  Users,
  UserPlus,
  BarChart3,
  Activity,
  Search,
  Filter,
} from "lucide-react";
import {
  listUsers,
  createUser,
  setUserStatus,
  updateUser,
  deleteUser,
} from "../../api/users";

export default function UsersOverview() {
  const [users, setUsers] = useState([]);
  const [creating, setCreating] = useState(false);
  const [form, setForm] = useState({
    nic: "",
    role: "Operator",
    isActive: true,
  });
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    (async () => {
      try {
        const data = await listUsers();
        setUsers(data);
      } catch {}
    })();
  }, []);

  const columns = ["NIC", "Role", "Status", "Actions"];

  const filteredUsers = users.filter(
    (user) =>
      user.nic.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.role.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="flex flex-col gap-8">
      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {/* EV Owners */}
        <div className="bg-white rounded-xl shadow-md hover:shadow-lg transition-shadow p-6 border-l-4 border-blue-500">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-sm font-medium text-gray-500 uppercase tracking-wide">
                Total EV Owners
              </h3>
              <p className="text-3xl font-bold text-blue-600 mt-3">1,234</p>
            </div>
            <div className="bg-blue-50 p-3 rounded-lg">
              <Users className="text-blue-500" size={32} />
            </div>
          </div>
        </div>

        {/* Total Operators */}
        <div className="bg-white rounded-xl shadow-md hover:shadow-lg transition-shadow p-6 border-l-4 border-green-500">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-sm font-medium text-gray-500 uppercase tracking-wide">
                Total Operators
              </h3>
              <p className="text-3xl font-bold text-green-600 mt-3">89</p>
            </div>
            <div className="bg-green-50 p-3 rounded-lg">
              <UserPlus className="text-green-500" size={32} />
            </div>
          </div>
        </div>

        {/* Active Users */}
        <div className="bg-white rounded-xl shadow-md hover:shadow-lg transition-shadow p-6 border-l-4 border-purple-500">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-sm font-medium text-gray-500 uppercase tracking-wide">
                Active Users
              </h3>
              <p className="text-3xl font-bold text-purple-600 mt-3">956</p>
            </div>
            <div className="bg-purple-50 p-3 rounded-lg">
              <Activity className="text-purple-500" size={32} />
            </div>
          </div>
        </div>
      </div>

      {/* User Management Section */}
      <div className="bg-white rounded-xl shadow-md">
        {/* Header */}
        <div className="p-6 border-b border-gray-200">
          <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
            <div>
              <h3 className="text-2xl font-bold text-gray-800">
                User Management
              </h3>
            </div>
            <button
              onClick={() => setCreating(true)}
              className="bg-[#347928] hover:bg-green-800 text-white px-6 py-3 rounded-lg font-medium transition-all shadow-sm hover:shadow-md flex items-center justify-center gap-2 cursor-pointer"
            >
              <UserPlus size={20} />
              <span>Create User</span>
            </button>
          </div>
        </div>

        {/* Create User Form */}
        {creating && (
          <div className="p-6 border-b border-gray-200">
            <h4 className="text-lg font-semibold text-gray-800 mb-4">
              Create New User
            </h4>
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  NIC
                </label>
                <input
                  className="w-full border border-gray-300 rounded-lg p-3  outline-none transition-all"
                  placeholder="Enter NIC"
                  value={form.nic}
                  onChange={(e) => setForm({ ...form, nic: e.target.value })}
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Role
                </label>
                <select
                  className="w-full border border-gray-300 rounded-lg p-3  outline-none transition-all"
                  value={form.role}
                  onChange={(e) => setForm({ ...form, role: e.target.value })}
                >
                  <option value="Operator">Operator</option>
                  <option value="Backoffice">Backoffice</option>
                  <option value="Owner">Owner</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Status
                </label>
                <select
                  className="w-full border border-gray-300 rounded-lg p-3 outline-none transition-all"
                  value={form.isActive ? "true" : "false"}
                  onChange={(e) =>
                    setForm({ ...form, isActive: e.target.value === "true" })
                  }
                >
                  <option value="true">Active</option>
                  <option value="false">Inactive</option>
                </select>
              </div>
              <div className="flex items-end gap-2">
                <button
                  className="flex-1 bg-[#347928] hover:bg-green-800 text-white px-4 py-3 rounded-lg font-medium transition-all shadow-sm hover:shadow-md cursor-pointer"
                  onClick={async () => {
                    try {
                      await createUser(form);
                      const data = await listUsers();
                      setUsers(data);
                      setCreating(false);
                      setForm({ nic: "", role: "Operator", isActive: true });
                    } catch (e) {
                      alert(e.message);
                    }
                  }}
                >
                  Save
                </button>
                <button
                  className="flex-1 bg-gray-200 hover:bg-gray-300 text-gray-700 px-4 py-3 rounded-lg font-medium transition-all cursor-pointer"
                  onClick={() => setCreating(false)}
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        )}

        

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
              {filteredUsers.length > 0 ? (
                filteredUsers.map((user) => (
                  <tr
                    key={user.nic}
                    className="hover:bg-gray-50 transition-colors"
                  >
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className="text-sm font-medium text-gray-900">
                        {user.nic}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <select
                        className="text-sm border border-gray-300 rounded-lg px-3 py-2  outline-none transition-all cursor-pointer"
                        value={user.role}
                        onChange={async (e) => {
                          const updated = { ...user, role: e.target.value };
                          await updateUser(user.nic, updated);
                          const data = await listUsers();
                          setUsers(data);
                        }}
                      >
                        <option value="Backoffice">Backoffice</option>
                        <option value="Operator">Operator</option>
                        <option value="Owner">Owner</option>
                      </select>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span
                        className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium ${
                          user.isActive
                            ? "bg-green-100 text-green-800"
                            : "bg-red-100 text-red-800"
                        }`}
                      >
                        {user.isActive ? "Active" : "Inactive"}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex gap-2">
                        <button
                          className="px-4 py-2 bg-[#347928] hover:bg-green-800 text-white text-sm rounded-lg transition-all shadow-sm hover:shadow-md cursor-pointer"
                          onClick={async () => {
                            const newActive = !user.isActive;
                            await setUserStatus(user.nic, newActive);
                            const data = await listUsers();
                            setUsers(data);
                          }}
                        >
                          Toggle Status
                        </button>
                        <button
                          className="px-4 py-2 bg-red-600 hover:bg-red-700 text-white text-sm rounded-lg transition-all shadow-sm hover:shadow-md cursor-pointer"
                          onClick={async () => {
                            if (
                              !confirm(
                                `Are you sure you want to delete user ${user.nic}?`
                              )
                            )
                              return;
                            try {
                              await deleteUser(user.nic);
                              const data = await listUsers();
                              setUsers(data);
                            } catch (e) {
                              alert(e.message);
                            }
                          }}
                        >
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={4} className="px-6 py-12 text-center">
                    <div className="flex flex-col items-center justify-center text-gray-400">
                      <Users size={48} className="mb-4" />
                      <p className="text-lg font-medium">No users found</p>
                      <p className="text-sm">
                        Try adjusting your search criteria
                      </p>
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
            Showing <span className="font-medium">{filteredUsers.length}</span>{" "}
            of <span className="font-medium">{users.length}</span> users
          </p>
        </div>
      </div>
    </div>
  );
}
