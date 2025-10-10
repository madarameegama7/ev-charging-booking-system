import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { Menu, X, LogOut, User as UserIcon } from "lucide-react";
import ev from "../assets/common/EV.png";
import { getCurrentUser, logout } from "../api/auth";

export default function Navbar() {
  const navigate = useNavigate();
  const location = useLocation();
  const [activeTab, setActiveTab] = useState("home");
  const [isVisible, setIsVisible] = useState(true);
  const [lastScrollY, setLastScrollY] = useState(0);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [user, setUser] = useState(null);
  const [showUserMenu, setShowUserMenu] = useState(false);

  const navItems = [
    { id: "home", label: "Home", path: "/" },
    { id: "about", label: "About Us", path: "/aboutus" },
    { id: "contact", label: "Contact Us", path: "/contactus" },
  ];

  // Check if user is logged in
  useEffect(() => {
    const currentUser = getCurrentUser();
    setUser(currentUser);
  }, [location.pathname]);

  // Set active tab based on current route
  useEffect(() => {
    const currentPath = location.pathname;
    if (currentPath === "/") {
      setActiveTab("home");
    } else if (currentPath === "/aboutus") {
      setActiveTab("about");
    } else if (currentPath === "/contactus") {
      setActiveTab("contact");
    }
  }, [location.pathname]);

  useEffect(() => {
    const handleScroll = () => {
      const currentScrollY = window.scrollY;

      if (currentScrollY === 0) {
        setIsVisible(true);
      } else if (currentScrollY > lastScrollY && currentScrollY > 100) {
        setIsVisible(false);
        setIsMobileMenuOpen(false);
        setShowUserMenu(false);
      } else if (currentScrollY < lastScrollY) {
        setIsVisible(true);
      }

      setLastScrollY(currentScrollY);
    };

    window.addEventListener("scroll", handleScroll, { passive: true });

    return () => {
      window.removeEventListener("scroll", handleScroll);
    };
  }, [lastScrollY]);

  const handleNavClick = (item) => {
    if (item.path) {
      setActiveTab(item.id);
      navigate(item.path);
      setIsMobileMenuOpen(false);
    }
  };

  const toggleMobileMenu = () => {
    setIsMobileMenuOpen(!isMobileMenuOpen);
    setShowUserMenu(false);
  };

  const handleLogout = () => {
    logout();
    setUser(null);
    setShowUserMenu(false);
    navigate("/");
  };

  const getInitials = () => {
    if (!user || !user.firstName || !user.lastName) return "";
    return (user.firstName.charAt(0) + user.lastName.charAt(0)).toUpperCase();
  };

  return (
    <nav
      className={`fixed top-0 left-0 right-0 z-50
        duration-300 ease-in-out ${
          isVisible
            ? "transform translate-y-6"
            : "transform -translate-y-[calc(100%+1.5rem)]"
        }`}
    >
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="bg-[#347928]/15 backdrop-blur-sm rounded-xl shadow-lg px-4 sm:px-6 py-3 sm:py-4">
          <div className="flex items-center justify-between">
            {/* Logo */}
            <div className="flex-shrink-0">
              <div
                className="cursor-pointer"
                onClick={() => {
                  navigate("/");
                  setIsMobileMenuOpen(false);
                }}
              >
                <img
                  src={ev}
                  alt="logo"
                  width={40}
                  height={40}
                  className="rounded-full"
                />
              </div>
            </div>

            {/* Desktop Navigation - Hidden on mobile */}
            <div className="hidden lg:flex items-center space-x-1">
              {navItems.map((item) => (
                <button
                  key={item.id}
                  onClick={() => handleNavClick(item)}
                  disabled={!item.path}
                  className={`
                    px-4 xl:px-6 py-2 rounded-full font-medium transition-all duration-300 relative text-sm xl:text-base
                    ${
                      activeTab === item.id
                        ? "text-black font-semibold"
                        : "text-black"
                    }
                    ${
                      item.path
                        ? "cursor-pointer group"
                        : "cursor-default opacity-50"
                    }
                  `}
                >
                  {item.label}
                  {item.path && (
                    <span className="absolute bottom-0 left-1/2 transform -translate-x-1/2 w-0 h-0.5 bg-black transition-all duration-300 group-hover:w-3/4"></span>
                  )}
                </button>
              ))}
            </div>

            {/* User Profile or Get Started Button - Desktop */}
            <div className="hidden lg:block relative">
              {user ? (
                <div>
                  <button
                    onClick={() => setShowUserMenu(!showUserMenu)}
                    className="w-10 h-10 rounded-full bg-[#347928] text-white font-semibold flex items-center justify-center hover:bg-green-800 transition-colors shadow-lg cursor-pointer"
                  >
                    {getInitials()}
                  </button>

                  {/* Dropdown Menu */}
                  {showUserMenu && (
                    <div className="absolute right-0 mt-2 w-56 bg-white rounded-lg shadow-xl py-2 z-50">
                      <button
                        onClick={() => {
                          setShowUserMenu(false);
                          navigate("/profile");
                        }}
                        className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2 cursor-pointer"
                      >
                        <UserIcon size={16} />
                        Profile
                      </button>
                      <button
                        onClick={() => {
                          setShowUserMenu(false);
                          navigate(
                            user.role === "Backoffice"
                              ? "/admin/dashboard"
                              : "/operator/dashboard"
                          );
                        }}
                        className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2 cursor-pointer"
                      >
                        <UserIcon size={16} />
                        Dashboard
                      </button>
                      <button
                        onClick={handleLogout}
                        className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-gray-100 flex items-center gap-2 cursor-pointer"
                      >
                        <LogOut size={16} />
                        Logout
                      </button>
                    </div>
                  )}
                </div>
              ) : (
                <button
                  className="bg-[#347928] text-white px-4 xl:px-6 py-2 rounded-full shadow-lg hover:shadow-xl cursor-pointer hover:bg-green-800 text-sm xl:text-base"
                  onClick={() => navigate("/login")}
                >
                  Get started
                </button>
              )}
            </div>

            {/* Mobile Menu Button */}
            <button
              className="lg:hidden p-2 rounded-full hover:bg-white/20 transition-colors duration-200"
              onClick={toggleMobileMenu}
              aria-label="Toggle menu"
            >
              {isMobileMenuOpen ? (
                <X className="w-6 h-6 text-black" />
              ) : (
                <Menu className="w-6 h-6 text-black" />
              )}
            </button>
          </div>

          {/* Mobile Menu - Dropdown */}
          <div
            className={`lg:hidden overflow-hidden transition-all duration-300 ease-in-out ${
              isMobileMenuOpen ? "max-h-96 mt-4" : "max-h-0"
            }`}
          >
            <div className="py-2 space-y-1">
              {navItems.map((item) => (
                <button
                  key={item.id}
                  onClick={() => handleNavClick(item)}
                  disabled={!item.path}
                  className={`
                    w-full text-left px-4 py-3 rounded-lg font-medium transition-all duration-200
                    ${
                      activeTab === item.id
                        ? "bg-white/30 text-black font-semibold"
                        : "text-black hover:bg-white/20"
                    }
                    ${
                      item.path ? "cursor-pointer" : "cursor-default opacity-50"
                    }
                  `}
                >
                  {item.label}
                </button>
              ))}

              {/* User Profile or Get Started Button - Mobile */}
              {user ? (
                <div className="space-y-2 pt-2 border-t border-white/20">
                  <div className="px-4 py-2 bg-white/20 rounded-lg">
                    <p className="text-sm font-semibold text-black">
                      {user.firstName} {user.lastName}
                    </p>
                    <p className="text-xs text-gray-700 mt-1">{user.role}</p>
                  </div>
                  <button
                    onClick={() => {
                      setIsMobileMenuOpen(false);
                      navigate("/profile");
                    }}
                    className="w-full text-left px-4 py-3 rounded-lg text-black hover:bg-white/20 flex items-center gap-2"
                  >
                    <UserIcon size={16} />
                    Profile
                  </button>
                  <button
                    onClick={() => {
                      setIsMobileMenuOpen(false);
                      navigate(
                        user.role === "Backoffice"
                          ? "/admin/dashboard"
                          : "/operator/dashboard"
                      );
                    }}
                    className="w-full text-left px-4 py-3 rounded-lg text-black hover:bg-white/20 flex items-center gap-2"
                  >
                    <UserIcon size={16} />
                    Dashboard
                  </button>
                  <button
                    onClick={handleLogout}
                    className="w-full text-left px-4 py-3 rounded-lg text-red-600 hover:bg-white/20 flex items-center gap-2"
                  >
                    <LogOut size={16} />
                    Logout
                  </button>
                </div>
              ) : (
                <button
                  className="w-full bg-[#347928] text-white px-4 py-3 rounded-lg shadow-lg hover:shadow-xl cursor-pointer transition-all duration-300 font-medium mt-2"
                  onClick={() => {
                    navigate("/login");
                    setIsMobileMenuOpen(false);
                  }}
                >
                  Get started
                </button>
              )}
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
}
