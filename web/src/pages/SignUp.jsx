import singinimage from "../assets/common/signinimage.jpg";
import { Eye, EyeOff, Mail, Lock, User, Phone } from "lucide-react";
import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { register } from "../api/auth";

export default function SignUp() {
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [nic, setNic] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [role, setRole] = useState("Operator"); // Add role state
  const [agreeToTerms, setAgreeToTerms] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  const toggleConfirmPasswordVisibility = () => {
    setShowConfirmPassword(!showConfirmPassword);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validation
    if (!firstName || !lastName || !email || !nic || !password || !role) {
      alert("Please fill in all required fields");
      return;
    }

    if (password !== confirmPassword) {
      alert("Passwords do not match");
      return;
    }

    if (!agreeToTerms) {
      alert("Please agree to the Terms of Service and Privacy Policy");
      return;
    }

    setIsLoading(true);

    try {
      const userData = {
        firstName,
        lastName,
        email,
        phone,
        nic,
        password,
        role // Add role to userData
      };

      await register(userData);
      alert("Account created successfully!");
      
      // Navigate based on role
      if (role === "Backoffice") {
        navigate("/admin/dashboard");
      } else if (role === "Operator") {
        navigate("/operator/dashboard");
      } else {
        navigate("/");
      }
    } catch (error) {
      alert(error.message || "Registration failed. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center p-4 bg-gray-50">
      <div className="w-full max-w-6xl bg-white rounded-xl overflow-hidden grid grid-cols-1 md:grid-cols-2">
        {/* left column-Image*/}
        <div className="hidden md:block">
          <img
            src={singinimage}
            alt="signup image"
            className="h-full w-full object-cover"
          />
        </div>

        {/* Right column */}
        <div className="p-6 sm:p-8">
          <div className="text-center mb-6">
            <h2 className="text-2xl sm:text-3xl font-bold text-gray-800">
              Create Account
            </h2>
            <p className="text-gray-400 text-sm mt-2">
              Welcome! Let's set up your account
            </p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            {/* First Name & Last Name */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label
                  htmlFor="firstName"
                  className="block text-sm font-medium text-gray-700"
                >
                  First Name *
                </label>
                <div className="flex items-center border border-gray-300 rounded-lg px-3 py-2 hover:border-gray-400 focus-within:border-black">
                  <User size={18} className="text-gray-400 mr-2" />
                  <input
                    id="firstName"
                    type="text"
                    value={firstName}
                    onChange={(e) => setFirstName(e.target.value)}
                    placeholder="John"
                    required
                    className="w-full text-sm focus:outline-none placeholder:text-gray-400"
                  />
                </div>
              </div>

              <div>
                <label
                  htmlFor="lastName"
                  className="block text-sm font-medium text-gray-700"
                >
                  Last Name *
                </label>
                <div className="flex items-center border border-gray-300 rounded-lg px-3 py-2 hover:border-gray-400 focus-within:border-black">
                  <User size={18} className="text-gray-400 mr-2" />
                  <input
                    id="lastName"
                    type="text"
                    value={lastName}
                    onChange={(e) => setLastName(e.target.value)}
                    placeholder="Doe"
                    required
                    className="w-full text-sm focus:outline-none placeholder:text-gray-400"
                  />
                </div>
              </div>
            </div>

            {/* NIC */}
            <div>
              <label
                htmlFor="nic"
                className="block text-sm font-medium text-gray-700"
              >
                NIC *
              </label>
              <div className="flex items-center border border-gray-300 rounded-lg px-3 py-2 hover:border-gray-400 focus-within:border-black">
                <User size={18} className="text-gray-400 mr-2" />
                <input
                  id="nic"
                  type="text"
                  value={nic}
                  onChange={(e) => setNic(e.target.value)}
                  placeholder="123456789V"
                  required
                  className="w-full text-sm focus:outline-none placeholder:text-gray-400"
                />
              </div>
            </div>

            {/* Email */}
            <div>
              <label
                htmlFor="email"
                className="block text-sm font-medium text-gray-700"
              >
                Email Address *
              </label>
              <div className="flex items-center border border-gray-300 rounded-lg px-3 py-2 hover:border-gray-400 focus-within:border-black">
                <Mail size={18} className="text-gray-400 mr-2" />
                <input
                  id="email"
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="name@gmail.com"
                  required
                  className="w-full text-sm focus:outline-none placeholder:text-gray-400"
                />
              </div>
            </div>

            {/* Phone (Optional) */}
            <div>
              <label
                htmlFor="phone"
                className="block text-sm font-medium text-gray-700"
              >
                Phone Number (Optional)
              </label>
              <div className="flex items-center border border-gray-300 rounded-lg px-3 py-2 hover:border-gray-400 focus-within:border-black">
                <Phone size={18} className="text-gray-400 mr-2" />
                <input
                  id="phone"
                  type="tel"
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                  placeholder="+94 77 123 4567"
                  className="w-full text-sm focus:outline-none placeholder:text-gray-400"
                />
              </div>
            </div>

            {/* Role Selection */}
            <div>
              <label htmlFor="role" className="block text-sm font-medium text-gray-700">
                Role *
              </label>
              <select
                id="role"
                value={role}
                onChange={(e) => setRole(e.target.value)}
                required
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:border-black focus:outline-none hover:border-gray-400"
              >
                <option value="Operator">Station Operator</option>
                <option value="Backoffice">Backoffice</option>
              </select>
            </div>

            {/* Password */}
            <div>
              <label
                htmlFor="password"
                className="block text-sm font-medium text-gray-700"
              >
                Password *
              </label>
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
                  required
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

            {/* Confirm Password */}
            <div>
              <label
                htmlFor="confirmPassword"
                className="block text-sm font-medium text-gray-700"
              >
                Confirm Password *
              </label>
              <div className="relative mt-1">
                <Lock
                  size={18}
                  className="absolute left-3 top-2.5 text-gray-400"
                />
                <input
                  id="confirmPassword"
                  type={showConfirmPassword ? "text" : "password"}
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  placeholder="••••••••"
                  required
                  className="w-full pl-10 pr-10 py-2 border border-gray-300 rounded-lg focus:border-black focus:outline-none"
                />
                <button
                  type="button"
                  onClick={toggleConfirmPasswordVisibility}
                  className="absolute right-3 top-2.5 text-gray-400 hover:text-gray-600"
                >
                  {showConfirmPassword ? (
                    <EyeOff size={18} />
                  ) : (
                    <Eye size={18} />
                  )}
                </button>
              </div>
            </div>

            {/* Terms and Conditions */}
            <div className="flex items-start">
              <input
                type="checkbox"
                id="agreeToTerms"
                checked={agreeToTerms}
                onChange={(e) => setAgreeToTerms(e.target.checked)}
                required
                className="w-4 h-4 text-black border-gray-300 rounded mt-0.5"
              />
              <label
                htmlFor="agreeToTerms"
                className="ml-2 text-sm text-gray-700"
              >
                I agree to the{" "}
                <Link
                  to="/terms"
                  className="text-blue-500 underline hover:text-blue-700"
                >
                  Terms of Service
                </Link>{" "}
                and{" "}
                <Link
                  to="/privacy"
                  className="text-blue-500 underline hover:text-blue-700"
                >
                  Privacy Policy
                </Link>
              </label>
            </div>

            {/* Sign Up button */}
            <button 
              type="submit"
              disabled={isLoading}
              className="w-full flex items-center justify-center py-3 bg-[#347928] text-white rounded-lg hover:bg-green-800 cursor-pointer transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isLoading ? "Creating Account..." : "Create Account"}
            </button>

            {/* Login Link */}
            <p className="text-center text-sm text-gray-600">
              Already have an account?{" "}
              <Link
                to="/login"
                className="text-blue-500 underline hover:text-blue-700"
              >
                Sign in here
              </Link>
            </p>
          </form>
        </div>
      </div>
    </div>
  );
}