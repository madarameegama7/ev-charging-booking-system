import React, { useState, useEffect } from 'react';

export default function Navbar() {
  const [activeTab, setActiveTab] = useState('home');
  const [isVisible, setIsVisible] = useState(true);
  const [lastScrollY, setLastScrollY] = useState(0);

  const navItems = [
    { id: 'home', label: 'Home' },
    { id: 'about', label: 'About Us' },
    { id: 'contact', label: 'Contact Us' },
    { id: 'dashboard', label: 'Dashboard' }
  ];

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
      }
      // Show navbar when scrolling up
      else if (currentScrollY < lastScrollY) {
        setIsVisible(true);
      }
      
      setLastScrollY(currentScrollY);
    };

    // Add scroll event listener
    window.addEventListener('scroll', handleScroll, { passive: true });
    
    // Cleanup function to remove event listener
    return () => {
      window.removeEventListener('scroll', handleScroll);
    };
  }, [lastScrollY]);

  return (
    <nav className={`fixed top-0 left-0 right-0 z-50 transition-transform duration-300 ease-in-out ${
      isVisible ? 'transform translate-y-6' : 'transform -translate-y-[calc(100%+1.5rem)]'
    }`}>
      <div className="max-w-7xl mx-auto flex items-center justify-between">
        {/* Logo */}
        <div className="flex">
          <h1>Logo</h1>
        </div>
        
        {/* Navigation Tabs - Centered */}
        <div className="absolute left-1/2 transform -translate-x-1/2">
          <div className="rounded-full px-2 py-2 flex items-center space-x-2 border border-black">
            {navItems.map((item) => (
              <button
                key={item.id}
                onClick={() => setActiveTab(item.id)}
                className={`
                  px-6 py-1 rounded-full font-medium transition-all duration-300
                  ${activeTab === item.id 
                    ? 'bg-gray-500 text-white' 
                    : 'text-black cursor-pointer'
                  }
                `}
              >
                {item.label}
              </button>
            ))}
          </div>
        </div>
        
        {/* Get Started Button */}
        <button className="bg-black text-white hover:bg-gray-800 px-6 py-2 rounded-full shadow-lg hover:shadow-xl cursor-pointer transition-all duration-300 transform hover:scale-105">
          Get started
        </button>
      </div>
    </nav>
  );
}