import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { changePassword } from '../api/users';

export default function ChangePassword() {
  const [newPassword, setNewPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const navigate = useNavigate();

  const nic = localStorage.getItem('nic');

  const onSubmit = async (e) => {
    e.preventDefault();
    if (!nic) return alert('No user nic found');
    if (!newPassword || newPassword.length < 6) return alert('Password must be at least 6 characters');
    if (newPassword !== confirm) return alert('Passwords do not match');
    try {
      await changePassword(nic, newPassword);
      alert('Password changed, please login again');
      localStorage.removeItem('token');
      navigate('/login');
    } catch (e) {
      alert(e.message || e);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 p-4">
      <form onSubmit={onSubmit} className="w-full max-w-md bg-white rounded-lg p-6 shadow">
        <h2 className="text-xl font-bold mb-4">Create a new password</h2>
        <div className="mb-3">
          <label className="block text-sm font-medium">New password</label>
          <input type="password" value={newPassword} onChange={(e)=>setNewPassword(e.target.value)} className="w-full border p-2 rounded" />
        </div>
        <div className="mb-3">
          <label className="block text-sm font-medium">Confirm</label>
          <input type="password" value={confirm} onChange={(e)=>setConfirm(e.target.value)} className="w-full border p-2 rounded" />
        </div>
        <div className="flex gap-2">
          <button className="flex-1 bg-[#347928] text-white py-2 rounded">Set password</button>
          <button type="button" onClick={()=>navigate('/login')} className="flex-1 bg-gray-200 py-2 rounded">Cancel</button>
        </div>
      </form>
    </div>
  );
}
