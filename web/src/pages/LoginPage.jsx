import singinimage from "../assets/common/signinimage.jpg";
import { Eye, EyeOff, Mail, Lock } from "lucide-react";
import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { login } from "../api/auth";

export default function LoginPage() {
  const [showPassword, setShowPassword] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("Backoffice");
  const navigate = useNavigate();
  const onSubmit = async (e) => {
    e.preventDefault();
    try {
      // Map email/password UI to NIC-based demo login
      // For demo: email box will be used for NIC, and Backoffice role by default
      const nic = email.trim();
      await login(nic, role);
      if (role === "Backoffice") navigate("/admin/dashboard");
      else if (role === "Operator") navigate("/operator/dashboard");
      else navigate("/home");
    } catch (err) {
      alert("Login failed");
    }
  };

  // Add the missing function
  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  return (
    <div className="flex min-h-screen items-center justify-center p-4 bg-gray-50">
      <div className="w-full max-w-6xl bg-white rounded-xl overflow-hidden grid grid-cols-1 md:grid-cols-2">
        {/* left column-Image*/}
        <div className="hidden md:block">
          <img
            src={singinimage}
            alt="signin image"
            className="h-full w-full object-cover"
          />
        </div>

        {/* Right column */}
        <div className="p-6 sm:p-8">
          <div className="text-center mb-6">
            <h2 className="text-2xl sm:text-3xl font-bold text-gray-800">
              Welcome back
            </h2>
            <p className="text-gray-500 text-sm mt-2">
              Please sign in to your account
            </p>
          </div>

          <div className="space-y-5">
            {/* Email */}
            <div>
              <label
                htmlFor="email"
                className="block text-sm font-medium text-gray-700"
              >
                NIC
              </label>
              <div className="flex items-center border border-gray-300 rounded-lg px-3 py-2 hover:border-gray-400 focus-within:border-black">
                <Mail size={18} className="text-gray-400 mr-2" />
                <input
                  id="email"
                  type="text"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="e.g. 123456789V"
                  className="w-full text-sm focus:outline-none placeholder:text-gray-400"
                />
              </div>
            </div>

            {/* Role */}
            <div>
              <label className="block text-sm font-medium text-gray-700">Role</label>
              <select
                className="w-full border border-gray-300 rounded-lg px-3 py-2"
                value={role}
                onChange={(e)=>setRole(e.target.value)}
              >
                <option value="Backoffice">Backoffice</option>
                <option value="Owner">Owner</option>
                <option value="Operator">Operator</option>
              </select>
            </div>

            {/*Password*/}
            <div>
              <div className="flex items-center justify-between">
                <label
                  htmlFor="password"
                  className="text-sm font-medium text-gray-700"
                >
                  Password (not required)
                </label>
              </div>
              <div className="relative mt-1">
                <Lock
                  size={18}
                  className="absolute left-3 top-2.5 text-gray-400"
                />
                <input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  className="w-full pl-10 pr-10 py-2 border border-gray-300 rounded-lg focus:border-black focus:outline-none"
                />
                <button
                  type="button"
                  onClick={togglePasswordVisibility}
                  className="absolute right-3 top-2.5 text-gray-400 hover:text-gray-600"
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>

            {/*Remember Me*/}
            <div className="flex items-center">
              <input
                type="checkbox"
                id="remember"
                className="w-4 h-4 text-black border-gray-300 rounded"
              />
              <label htmlFor="remember" className="ml-2 text-sm text-gray-700">
                Remember me
              </label>
            </div>

            {/* SignIn button */}
            <button onClick={onSubmit} className="w-full flex items-center justify-center py-3 bg-[#347928] text-white rounded-lg hover:bg-green-800 cursor-pointer transition-colors">
              Sign In
            </button>

            {/* Signup Link */}
            <p className="text-center text-sm text-gray-600">
              Don't have an account?{" "}
              <Link
                to="/signup"
                className="text-blue-500 underline hover:text-blue-700"
              >
                Sign up now
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
