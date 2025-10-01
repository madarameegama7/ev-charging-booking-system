import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { Menu, X } from "lucide-react";
import ev from "../assets/common/EV.png"

export default function Navbar() {
  const navigate = useNavigate();
  const location = useLocation();
  const [activeTab, setActiveTab] = useState("home");
  const [isVisible, setIsVisible] = useState(true);
  const [lastScrollY, setLastScrollY] = useState(0);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const navItems = [
    { id: "home", label: "Home", path: "/" },
    { id: "about", label: "About Us", path: "/aboutus" },
    { id: "contact", label: "Contact Us", path: "/contactus" },
    { id: "dashboard", label: "Dashboard", path: null },
  ];

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

      // Show navbar when at the very top
      if (currentScrollY === 0) {
        setIsVisible(true);
      }
      // Hide navbar when scrolling down
      else if (currentScrollY > lastScrollY && currentScrollY > 100) {
        setIsVisible(false);
        setIsMobileMenuOpen(false); // Close mobile menu when hiding navbar
      }
      // Show navbar when scrolling up
      else if (currentScrollY < lastScrollY) {
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
      setIsMobileMenuOpen(false); // Close mobile menu after navigation
    }
  };

  const toggleMobileMenu = () => {
    setIsMobileMenuOpen(!isMobileMenuOpen);
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
                  {/* Underline on hover - only for items with path */}
                  {item.path && (
                    <span className="absolute bottom-0 left-1/2 transform -translate-x-1/2 w-0 h-0.5 bg-black transition-all duration-300 group-hover:w-3/4"></span>
                  )}
                </button>
              ))}
            </div>

            {/* Get Started Button - Desktop */}
            <button
              className="hidden lg:block bg-[#347928] text-white px-4 xl:px-6 py-2 rounded-full shadow-lg hover:shadow-xl cursor-pointer hover:bg-green-800 text-sm xl:text-base"
              onClick={() => navigate("/login")}
            >
              Get started
            </button>

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
                    ${item.path ? "cursor-pointer" : "cursor-default opacity-50"}
                  `}
                >
                  {item.label}
                </button>
              ))}

              {/* Get Started Button - Mobile */}
              <button
                className="w-full bg-[#347928] text-white px-4 py-3 rounded-lg shadow-lg hover:shadow-xl cursor-pointer transition-all duration-300 font-medium mt-2"
                onClick={() => {
                  navigate("/login");
                  setIsMobileMenuOpen(false);
                }}
              >
                Get started
              </button>
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
}