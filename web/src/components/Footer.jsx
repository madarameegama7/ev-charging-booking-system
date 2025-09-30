import React, { useState } from 'react';
import { Facebook, Linkedin, Twitter, Youtube, Instagram, Mail, Phone, ArrowRight } from 'lucide-react';
import { Link } from 'react-router-dom';
import logo from "../assets/common/EV.png"

export default function EVOperatorFooter() {
  const [email, setEmail] = useState('');

  const handleSubmit = () => {
    if (email) {
      alert(`Subscribed with email: ${email}`);
      setEmail('');
    }
  };

  return (
    <footer className="bg-white text-gray-800 relative">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 md:py-16">
        {/* Main Header */}
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-12 pb-8 border-b border-gray-200">
          <h2 className="text-3xl md:text-4xl lg:text-5xl font-bold text-gray-900 mb-6 md:mb-0">
            Let's Connect there
          </h2>
          <button className="bg-[#347928] hover:bg-green-800 text-white px-8 py-3 rounded-lg flex items-center gap-2 transition-colors duration-200">
            Connect <ArrowRight size={20} />
          </button>
        </div>

        {/* Main Content Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8 lg:gap-12 mb-12">
          {/* Company Info */}
          <div className="lg:col-span-1">
            <div className="flex items-center mb-2">
              <div className="flex items-center justify-center">
                <img src={logo} alt="logo" width={80} height={80} />
              </div>
              <h3 className="text-xl font-bold text-gray-900">EVCharge</h3>
            </div>
            <p className="text-gray-600 mb-6 leading-relaxed">
              EV Charge specializes in providing comprehensive electric vehicle charging solutions for operators and businesses. We deliver scalable infrastructure, smart management systems, and reliable support to power the future of sustainable transportation.
            </p>
            <div className="flex gap-3">
              <a href="#" className="w-10 h-10 bg-[#347928] rounded-full flex items-center justify-center text-white transition-colors duration-200">
                <Instagram size={20} />
              </a>
              <a href="#" className="w-10 h-10 bg-[#347928] rounded-full flex items-center justify-center text-white transition-colors duration-200">
                <Facebook size={20} />
              </a>
              <a href="#" className="w-10 h-10 bg-[#347928] rounded-full flex items-center justify-center text-white transition-colors duration-200">
                <Linkedin size={20} />
              </a>
              <a href="#" className="w-10 h-10 bg-[#347928] rounded-full flex items-center justify-center text-white transition-colors duration-200">
                <Youtube size={20} />
              </a>
              <a href="#" className="w-10 h-10 bg-[#347928] rounded-full flex items-center justify-center text-white transition-colors duration-200">
                <Twitter size={20} />
              </a>
            </div>
          </div>

          {/* Navigation */}
          <div>
            <h4 className="text-lg font-semibold text-gray-900 mb-4">Navigation</h4>
            <ul className="space-y-3">
              <li>
                <Link to="/" className="text-gray-600 hover:text-gray-900">Home</Link>
              </li>
              <li>
                <Link to="/aboutus" className="text-gray-600 hover:text-gray-900">About</Link>
              </li>
              <li>
                <Link to="/contactus" className="text-gray-600 hover:text-gray-900">Contact Us</Link>
              </li>
            </ul>
          </div>

          {/* Contact */}
          <div>
            <h4 className="text-lg font-semibold text-gray-900 mb-4">Contact</h4>
            <ul className="space-y-4">
              <li>
                <a href="tel:+94112194350" className="flex items-center gap-2 text-gray-600">
                  <Phone size={18} />
                  <span>+94 (11) 2 19 4350</span>
                </a>
              </li>
              <li>
                <a href="mailto:info@evcharge.lk" className="flex items-center gap-2 text-gray-600">
                  <Mail size={18} />
                  <span>info@evcharge.lk</span>
                </a>
              </li>
            </ul>
          </div>

          {/* Newsletter */}
          <div>
            <h4 className="text-lg font-semibold text-gray-900 mb-4">Get the latest information</h4>
            <div className="flex gap-2">
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Email Address"
                className="flex-1 px-4 py-3 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-600 focus:border-transparent"
              />
              <button
                onClick={handleSubmit}
                className="bg-[#347928] text-white p-3 rounded-lg transition-colors duration-200"
              >
                <ArrowRight size={20} />
              </button>
            </div>
          </div>
        </div>

        {/* Bottom Bar */}
        <div className="pt-8 border-t border-gray-200 flex flex-col md:flex-row justify-between items-center gap-4">
          <p className="text-gray-600 text-sm text-center md:text-left">
            Copyright © 2025 EVCharge Technologies (Pvt) Ltd. All Rights Reserved.
          </p>
          <div className="flex items-center gap-6 text-sm">
            <a href="#" className="text-gray-600 underline">
              User Terms & Conditions
            </a>
            <span className="text-gray-400">|</span>
            <a href="#" className="text-gray-600 underline">
              Privacy Policy
            </a>
          </div>
        </div>
      </div>
    </footer>
  );
}
