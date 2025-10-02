import { useEffect, useState } from "react";
import { User, Mail, Lock, Edit, Phone as PhoneIcon, IdCard } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { API_BASE } from "../config";

export default function Profile() {
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [user, setUser] = useState(null);
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    phone: "",
    newPassword: "",
  });
  
  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const nic = localStorage.getItem("nic");

  const fetchUser = async () => {
    if (!token || !nic) {
      navigate("/login");
      return;
    }

    try {
      const response = await fetch(`${API_BASE}/api/User/${nic}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        if (response.status === 401) {
          localStorage.clear();
          navigate("/login");
          return;
        }
        throw new Error("Failed to fetch user data");
      }

      const userData = await response.json();
      setUser(userData);
      setFormData({
        name: userData.name || "",
        email: userData.email || "",
        phone: userData.phone || "",
        newPassword: "",
      });
      setLoading(false);
    } catch (error) {
      alert("Failed to fetch user data");
      console.error(error);
      setLoading(false);
    }
  };

  const handleUpdate = async (e) => {
    e.preventDefault();
    
    try {
      setUpdating(true);
      
      const updateData = {
        NIC: nic,
        Name: formData.name,
        Email: formData.email,
        Phone: formData.phone,
        Role: user.role,
        IsActive: user.isActive,
        PasswordHash: user.passwordHash, // Keep existing password hash
      };

      // If new password provided, hash it on backend
      if (formData.newPassword) {
        // We'll need to add password update logic to backend
        // For now, keep the existing hash
        alert("Password change feature coming soon");
        setUpdating(false);
        return;
      }

      const response = await fetch(`${API_BASE}/api/User/${nic}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(updateData),
      });

      if (!response.ok) {
        throw new Error("Update failed");
      }

      const updatedUser = await response.json();
      setUser(updatedUser);
      setIsEditing(false);
      alert("Profile updated successfully");
      
      setFormData({
        ...formData,
        newPassword: "",
      });
    } catch (error) {
      alert("Update failed. Please try again.");
      console.error(error);
    } finally {
      setUpdating(false);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  useEffect(() => {
    fetchUser();
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-xl text-gray-600">Loading...</div>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-xl text-gray-600">User not found</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-emerald-50 via-white to-green-50 py-12 px-4">
      <div className="max-w-4xl mx-auto">
        {/* Profile Header Card */}
        <div className="bg-white rounded-3xl shadow-xl overflow-hidden mb-8">
          <div className="bg-gradient-to-r from-[#347928] to-emerald-600 h-32"></div>
          <div className="px-8 pb-8">
            <div className="flex flex-col items-center -mt-16">
              <div className="w-32 h-32 rounded-full bg-white shadow-lg flex items-center justify-center text-5xl font-bold text-[#347928] border-4 border-white">
                {user.name?.charAt(0).toUpperCase() || "U"}
              </div>
              <h1 className="text-3xl font-bold text-gray-900 mt-4">{user.name || "User"}</h1>
              <span className="px-4 py-1 bg-[#347928]/10 text-[#347928] rounded-full text-sm font-semibold mt-2">
                {user.role}
              </span>
            </div>
          </div>
        </div>

        {/* Profile Details Card */}
        <div className="bg-white rounded-3xl shadow-xl p-8">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-[#347928]/10 rounded-xl flex items-center justify-center">
                <Edit size={20} className="text-[#347928]" />
              </div>
              <h2 className="text-2xl font-bold text-gray-900">Profile Information</h2>
            </div>
            {!isEditing && (
              <button
                onClick={() => setIsEditing(true)}
                className="px-4 py-2 bg-[#347928] text-white rounded-lg hover:bg-[#347928]/90 transition-colors cursor-pointer"
              >
                Edit Profile
              </button>
            )}
          </div>

          <form onSubmit={handleUpdate} className="space-y-6">
            {/* NIC (Read-only) */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                NIC
              </label>
              <div className="flex items-center border border-gray-300 rounded-lg px-4 py-3 bg-gray-50">
                <IdCard size={18} className="text-gray-400 mr-3" />
                <input
                  type="text"
                  value={user.nic}
                  disabled
                  className="w-full bg-transparent focus:outline-none text-gray-600"
                />
              </div>
            </div>

            {/* Name */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Full Name *
              </label>
              <div className={`flex items-center border rounded-lg px-4 py-3 ${isEditing ? 'border-gray-300 hover:border-gray-400' : 'border-gray-300 bg-gray-50'}`}>
                <User size={18} className="text-gray-400 mr-3" />
                <input
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  disabled={!isEditing}
                  required
                  className="w-full focus:outline-none bg-transparent"
                />
              </div>
            </div>

            {/* Email */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Email Address *
              </label>
              <div className={`flex items-center border rounded-lg px-4 py-3 ${isEditing ? 'border-gray-300 hover:border-gray-400' : 'border-gray-300 bg-gray-50'}`}>
                <Mail size={18} className="text-gray-400 mr-3" />
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  disabled={!isEditing}
                  required
                  className="w-full focus:outline-none bg-transparent"
                />
              </div>
            </div>

            {/* Phone */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Phone Number
              </label>
              <div className={`flex items-center border rounded-lg px-4 py-3 ${isEditing ? 'border-gray-300 hover:border-gray-400' : 'border-gray-300 bg-gray-50'}`}>
                <PhoneIcon size={18} className="text-gray-400 mr-3" />
                <input
                  type="tel"
                  name="phone"
                  value={formData.phone}
                  onChange={handleChange}
                  disabled={!isEditing}
                  placeholder="+94 77 123 4567"
                  className="w-full focus:outline-none bg-transparent"
                />
              </div>
            </div>

            {/* Action Buttons */}
            {isEditing && (
              <div className="flex gap-4 pt-6">
                <button
                  type="submit"
                  disabled={updating}
                  className="flex-1 bg-[#347928] text-white py-3 rounded-lg font-semibold hover:bg-[#347928]/90 transition-colors disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer"
                >
                  {updating ? "Updating..." : "Save Changes"}
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setIsEditing(false);
                    setFormData({
                      name: user.name || "",
                      email: user.email || "",
                      phone: user.phone || "",
                      newPassword: "",
                    });
                  }}
                  className="flex-1 bg-red-600 text-white py-3 rounded-lg font-semibold hover:bg-red-700 transition-colors cursor-pointer"
                >
                  Cancel
                </button>
              </div>
            )}
          </form>
        </div>
      </div>
    </div>
  );
}