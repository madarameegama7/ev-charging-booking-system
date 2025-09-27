import React, { useState } from 'react';

export default function Navbar() {
  const [activeTab, setActiveTab] = useState('home');

  const navItems = [
    { id: 'home', label: 'Home' },
    { id: 'about', label: 'About Us' },
    { id: 'contact', label: 'Contact Us' }
  ];

  return (
    <nav className="fixed top-0 left-0 right-0 z-50 mt-6">
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
        <button className=" bg-blue-400 text-white hover:bg-blue-500 px-6 py-2  rounded-full shadow-lg hover:shadow-xl cursor-pointer transition-all duration-300 transform hover:scale-105">
          Get started
        </button>
      </div>
    </nav>
  );
}