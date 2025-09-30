import React, { useState } from 'react';
import { Phone, Mail, Instagram, Facebook, Linkedin, Youtube, Send } from 'lucide-react';

export default function ContactPage() {
  const [formData, setFormData] = useState({
    name: '',
    phone: '',
    email: '',
    message: ''
  });

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = () => {
    if (formData.name && formData.phone && formData.email && formData.message) {
      alert('Thank you for contacting us! We will get back to you soon.');
      setFormData({ name: '', phone: '', email: '', message: '' });
    } else {
      alert('Please fill out all fields');
    }
  };

  return (
    <div className="min-h-screen px-4 sm:px-6 lg:px-8 py-8 sm:py-12">
      <div className="max-w-6xl mx-auto">
        {/* Header */}
        <h1 className="text-3xl sm:text-4xl md:text-5xl lg:text-6xl font-bold text-center text-gray-800 mt-8 sm:mt-12 lg:mt-20 px-4">
          Get in Touch with EVConnect
        </h1>

        {/* Social Media Icons */}
        <div className="flex flex-wrap justify-center gap-4 sm:gap-6 mt-8 sm:mt-10 px-4">
          <div className="bg-white p-3 sm:p-4 rounded-full shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-110 transform cursor-pointer">
            <Instagram className="w-5 h-5 sm:w-6 sm:h-6 text-pink-600" />
          </div>
          <div className="bg-white p-3 sm:p-4 rounded-full shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-110 transform cursor-pointer">
            <Facebook className="w-5 h-5 sm:w-6 sm:h-6 text-blue-600" />
          </div>
          <div className="bg-white p-3 sm:p-4 rounded-full shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-110 transform cursor-pointer">
            <Linkedin className="w-5 h-5 sm:w-6 sm:h-6 text-blue-700" />
          </div>
          <div className="bg-white p-3 sm:p-4 rounded-full shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-110 transform cursor-pointer">
            <Youtube className="w-5 h-5 sm:w-6 sm:h-6 text-red-600" />
          </div>
          <div className="bg-white p-3 sm:p-4 rounded-full shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-110 transform cursor-pointer">
            <svg className="w-5 h-5 sm:w-6 sm:h-6" viewBox="0 0 24 24" fill="currentColor">
              <path d="M19.59 6.69a4.83 4.83 0 0 1-3.77-4.25V2h-3.45v13.67a2.89 2.89 0 0 1-5.2 1.74 2.89 2.89 0 0 1 2.31-4.64 2.93 2.93 0 0 1 .88.13V9.4a6.84 6.84 0 0 0-1-.05A6.33 6.33 0 0 0 5 20.1a6.34 6.34 0 0 0 10.86-4.43v-7a8.16 8.16 0 0 0 4.77 1.52v-3.4a4.85 4.85 0 0 1-1-.1z"/>
            </svg>
          </div>
        </div>

        {/* Contact Info Boxes */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 sm:gap-6 mt-8 sm:mt-10">
          {/* Phone Box */}
          <div className="bg-white rounded-lg shadow-lg p-6 sm:p-8">
            <div className="flex items-center gap-3 mb-4">
              <Phone className="w-5 h-5 sm:w-6 sm:h-6 text-blue-600 flex-shrink-0" />
              <h3 className="text-lg sm:text-xl font-semibold text-gray-800">Phone</h3>
            </div>
            <p className="text-xl sm:text-2xl font-bold text-gray-900 mb-4 break-all">+1 (555) 123-4567</p>
            <div className="text-gray-600 text-sm sm:text-base">
              <p className="font-semibold">Monday - Sunday</p>
              <p>9:00 AM - 6:00 PM</p>
            </div>
          </div>

          {/* Email Box */}
          <div className="bg-white rounded-lg shadow-lg p-6 sm:p-8">
            <div className="flex items-center gap-3 mb-4">
              <Mail className="w-5 h-5 sm:w-6 sm:h-6 text-blue-600 flex-shrink-0" />
              <h3 className="text-lg sm:text-xl font-semibold text-gray-800">Email</h3>
            </div>
            <p className="text-xl sm:text-2xl font-bold text-gray-900 mb-4 break-all">contact@evconnect.com</p>
            <p className="text-gray-600 text-sm sm:text-base">We aim to respond within 24 hours.</p>
          </div>
        </div>

        {/* Contact Form */}
        <div className="bg-white rounded-lg shadow-lg p-6 sm:p-8 md:p-10 lg:p-12 mt-8 sm:mt-10">
          <p className="text-base sm:text-lg text-gray-700 mb-6 sm:mb-8 text-center font-medium px-2">
            <strong>Please fill out the form below, and we'll connect you with the best expert for your needs</strong>
          </p>

          <div className="space-y-4 sm:space-y-6">
            <div>
              <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-2">
                Name *
              </label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                className="w-full px-3 sm:px-4 py-2 sm:py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition text-sm sm:text-base"
                placeholder="Your full name"
              />
            </div>

            <div>
              <label htmlFor="phone" className="block text-sm font-medium text-gray-700 mb-2">
                Phone Number *
              </label>
              <input
                type="tel"
                id="phone"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                className="w-full px-3 sm:px-4 py-2 sm:py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition text-sm sm:text-base"
                placeholder="Your phone number"
              />
            </div>

            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-2">
                Email *
              </label>
              <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                className="w-full px-3 sm:px-4 py-2 sm:py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition text-sm sm:text-base"
                placeholder="your.email@example.com"
              />
            </div>

            <div>
              <label htmlFor="message" className="block text-sm font-medium text-gray-700 mb-2">
                Your Message *
              </label>
              <textarea
                id="message"
                name="message"
                value={formData.message}
                onChange={handleChange}
                rows="5"
                className="w-full px-3 sm:px-4 py-2 sm:py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition resize-none text-sm sm:text-base"
                placeholder="Tell us how we can help you..."
              />
            </div>

            <button
              type="button"
              onClick={handleSubmit}
              className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 sm:py-4 px-4 sm:px-6 rounded-lg transition duration-300 flex items-center justify-center gap-2 shadow-lg hover:shadow-xl cursor-pointer text-sm sm:text-base"
            >
              <Send className="w-4 h-4 sm:w-5 sm:h-5" />
              Submit
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}