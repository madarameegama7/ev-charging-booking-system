import React, { useState } from "react";
import { User, Mail, Lock, Edit, CreditCard } from "lucide-react";

export default function Profile() {
  const [isEditing, setIsEditing] = useState(false);
  const [profile, setProfile] = useState({
    name: "Alex Johnson",
    nic: "200050704365",
    email: "alex.johnson@email.com",
    currentPassword: "",
    newPassword: "",
  });

  const [tempProfile, setTempProfile] = useState(profile);

  const getInitials = () => {
    if (!profile.name) return "U";
    const names = profile.name.trim().split(" ");
    if (names.length === 1) {
      return names[0].substring(0, 2).toUpperCase();
    }
    return (
      names[0].charAt(0) + names[names.length - 1].charAt(0)
    ).toUpperCase();
  };

  const handleEditClick = () => {
    setTempProfile(profile);
    setIsEditing(true);
  };

  const handleCancelEdit = () => {
    setTempProfile(profile);
    setIsEditing(false);
  };

  const handleChange = (field, value) => {
    setTempProfile({ ...tempProfile, [field]: value });
  };

  const handleUpdate = () => {
    // Here you would make your API call to update the profile
    setProfile(tempProfile);
    setIsEditing(false);
    alert("Profile updated successfully");
  };

  if (isEditing) {
    // Edit/Update Page
    return (
      <div className="max-w-4xl mx-auto my-10 mt-26 px-4 relative">
        {/* Background decoration */}
        <div className="absolute -z-10 left-10 top-10 w-32 h-32 bg-emerald-100 rounded-full blur-3xl opacity-40"></div>
        <div className="absolute -z-10 right-10 bottom-10 w-40 h-40 bg-teal-100 rounded-full blur-3xl opacity-40"></div>

        <div className="bg-white border border-gray-200 rounded-2xl shadow-lg overflow-hidden py-10 px-6">
          {/* Header */}
          <div className="flex items-center gap-2 mb-6">
            <Edit size={20} className="text-teal-600" />
            <h2 className="text-2xl font-semibold text-black">
              Update Profile
            </h2>
          </div>

          <div className="space-y-6">
            {/* Name Field */}
            <div className="bg-gray-50 rounded-lg p-4">
              <label className="flex items-center gap-2 text-black font-medium mb-3">
                <User size={18} className="text-gray-600" />
                Name *
              </label>
              <input
                type="text"
                value={tempProfile.name}
                onChange={(e) => handleChange("name", e.target.value)}
                required
                className="w-full border-2 border-gray-300 rounded-lg px-4 py-3 hover:border-gray-400 focus:border-teal-500 focus:outline-none transition-all bg-white"
                placeholder="Enter your name"
              />
            </div>

            {/* NIC Field (Read-only) */}
            <div className="bg-gray-50 rounded-lg p-4">
              <label className="flex items-center gap-2 text-black font-medium mb-3">
                <CreditCard size={18} className="text-gray-600" />
                NIC
              </label>
              <input
                type="text"
                value={tempProfile.nic}
                disabled
                className="w-full border-2 border-gray-300 rounded-lg px-4 py-3 bg-gray-100 text-gray-500 cursor-not-allowed"
              />
            </div>

            {/* Email Field */}
            <div className="bg-gray-50 rounded-lg p-4">
              <label className="flex items-center gap-2 text-black font-medium mb-3">
                <Mail size={18} className="text-gray-600" />
                Email *
              </label>
              <input
                type="email"
                value={tempProfile.email}
                onChange={(e) => handleChange("email", e.target.value)}
                required
                className="w-full border-2 border-gray-300 rounded-lg px-4 py-3 hover:border-gray-400 focus:border-teal-500 focus:outline-none transition-all bg-white"
                placeholder="Enter your email"
              />
            </div>

            {/* Current Password Field */}
            <div className="bg-gray-50 rounded-lg p-4">
              <label className="flex items-center gap-2 text-black font-medium mb-3">
                <Lock size={18} className="text-gray-600" />
                Current Password
              </label>
              <input
                type="password"
                value={tempProfile.currentPassword}
                onChange={(e) =>
                  handleChange("currentPassword", e.target.value)
                }
                className="w-full border-2 border-gray-300 rounded-lg px-4 py-3 hover:border-gray-400 focus:border-teal-500 focus:outline-none transition-all bg-white"
                placeholder="Enter current password"
              />
            </div>

            {/* New Password Field */}
            <div className="bg-gray-50 rounded-lg p-4">
              <label className="flex items-center gap-2 text-black font-medium mb-3">
                <Lock size={18} className="text-gray-600" />
                New Password
              </label>
              <input
                type="password"
                value={tempProfile.newPassword}
                onChange={(e) => handleChange("newPassword", e.target.value)}
                className="w-full border-2 border-gray-300 rounded-lg px-4 py-3 hover:border-gray-400 focus:border-teal-500 focus:outline-none transition-all bg-white"
                placeholder="Enter new password"
              />
            </div>

            {/* Action Buttons */}
            <div className="flex gap-4 justify-center pt-4">
              <button
                onClick={handleUpdate}
                className="bg-[#347928] text-white hover:bg-[#347928]/80 px-8 py-3 rounded-lg shadow-md transition-all duration-300 font-medium"
              >
                Update Profile
              </button>
              <button
                onClick={handleCancelEdit}
                className="bg-gray-500 text-white hover:bg-gray-600 px-8 py-3 rounded-lg shadow-md transition-all duration-300 font-medium"
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // View Profile Page
  return (
    <div className="max-w-4xl mx-auto my-10 mt-26 px-4 relative">
      {/* Background decoration */}
      <div className="absolute -z-10 left-10 top-10 w-32 h-32 bg-emerald-100 rounded-full blur-3xl opacity-40"></div>
      <div className="absolute -z-10 right-10 bottom-10 w-40 h-40 bg-teal-100 rounded-full blur-3xl opacity-40"></div>

      <div className="bg-white border border-gray-200 rounded-2xl shadow-lg overflow-hidden py-10 px-6">
        {/* Profile Avatar with Edit Icon */}
        <div className="flex flex-col items-center mb-8">
          <div className="relative">
            <div className="w-32 h-32 rounded-full bg-emerald-100 flex items-center justify-center text-4xl font-bold text-black shadow-md">
              {getInitials()}
            </div>
            <button
              onClick={handleEditClick}
              className="absolute bottom-0 right-0 w-10 h-10 flex items-center justify-center transition-all cursor-pointer"
            >
              <Edit size={18} className="text-black" />
            </button>
          </div>
        </div>

        {/* User Information */}
        <div className="space-y-4 max-w-md mx-auto">
          {/* Name */}
          <div className="bg-gray-50 rounded-lg p-4">
            <div className="flex items-center gap-3">
              <User size={20} className="text-gray-600" />
              <div>
                <p className="text-xs text-gray-500 font-medium">Name</p>
                <p className="text-base text-black font-semibold">
                  {profile.name}
                </p>
              </div>
            </div>
          </div>

          {/* NIC */}
          <div className="bg-gray-50 rounded-lg p-4">
            <div className="flex items-center gap-3">
              <CreditCard size={20} className="text-gray-600" />
              <div>
                <p className="text-xs text-gray-500 font-medium">NIC</p>
                <p className="text-base text-black font-semibold">
                  {profile.nic}
                </p>
              </div>
            </div>
          </div>

          {/* Email */}
          <div className="bg-gray-50 rounded-lg p-4">
            <div className="flex items-center gap-3">
              <Mail size={20} className="text-gray-600" />
              <div>
                <p className="text-xs text-gray-500 font-medium">Email</p>
                <p className="text-base text-black font-semibold">
                  {profile.email}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
