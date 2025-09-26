import React, { useEffect, useState } from "react";
import { Users, UserPlus, BarChart3, Activity } from "lucide-react";
import { listUsers, createUser, setUserStatus, updateUser, deleteUser } from "../../api/users";

export default function UsersOverview() {
  const [users, setUsers] = useState([]);
  const [creating, setCreating] = useState(false);
  const [form, setForm] = useState({ nic: '', role: 'Operator', isActive: true });

  useEffect(() => {
    (async () => {
      try {
        const data = await listUsers();
        setUsers(data);
      } catch {}
    })();
  }, []);

  const columns = ["NIC", "Role", "Status", "Actions"];

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
          <button onClick={()=>setCreating(true)} className="bg-blue-500 hover:bg-blue-600 text-white px-6 py-3 rounded-lg font-medium transition-colors flex items-center cursor-pointer">
            <UserPlus size={20} className="mr-2" />
            Create User
          </button>
        </div>

        {creating && (
          <div className="mb-4 grid grid-cols-1 md:grid-cols-4 gap-2">
            <input className="border p-2" placeholder="NIC" value={form.nic} onChange={(e)=>setForm({...form, nic:e.target.value})} />
            <select className="border p-2" value={form.role} onChange={(e)=>setForm({...form, role:e.target.value})}>
              <option value="Operator">Operator</option>
              <option value="Backoffice">Backoffice</option>
              <option value="Owner">Owner</option>
            </select>
            <select className="border p-2" value={form.isActive? 'true':'false'} onChange={(e)=>setForm({...form, isActive:e.target.value==='true'})}>
              <option value="true">Active</option>
              <option value="false">Inactive</option>
            </select>
            <div className="flex gap-2">
              <button className="bg-green-600 text-white px-3" onClick={async()=>{
                try {
                  await createUser(form);
                  const data = await listUsers();
                  setUsers(data);
                  setCreating(false);
                  setForm({ nic:'', role:'Operator', isActive:true });
                } catch (e) { alert(e.message); }
              }}>Save</button>
              <button className="bg-gray-300 px-3" onClick={()=>setCreating(false)}>Cancel</button>
            </div>
          </div>
        )}

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
                {users.map((user) => (
                  <tr key={user.nic} className="border-b hover:bg-gray-50 transition-colors">
                    <td className="p-3">{user.nic}</td>
                    <td className="p-3">
                      <select className="border p-1" value={user.role} onChange={async(e)=>{
                        const updated = { ...user, role: e.target.value };
                        await updateUser(user.nic, updated);
                        const data = await listUsers();
                        setUsers(data);
                      }}>
                        <option value="Backoffice">Backoffice</option>
                        <option value="Operator">Operator</option>
                        <option value="Owner">Owner</option>
                      </select>
                    </td>
                    <td className="p-3">{user.isActive ? 'Active' : 'Inactive'}</td>
                    <td className="p-3 flex gap-2">
                      <button className="px-3 py-1 bg-blue-500 hover:bg-blue-600 text-white rounded transition-colors cursor-pointer" onClick={async()=>{
                        const newActive = !user.isActive;
                        await setUserStatus(user.nic, newActive);
                        const data = await listUsers();
                        setUsers(data);
                      }}>Toggle Active</button>
                      <button className="px-3 py-1 bg-red-600 hover:bg-red-700 text-white rounded transition-colors cursor-pointer" onClick={async()=>{
                        if (!confirm(`Delete ${user.nic}?`)) return;
                        try {
                          await deleteUser(user.nic);
                          const data = await listUsers();
                          setUsers(data);
                        } catch(e) { alert(e.message); }
                      }}>Delete</button>
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