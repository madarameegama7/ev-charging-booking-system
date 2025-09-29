import bgImage from "../assets/home/home4.jpg";
import { ArrowDown, Calendar, Search, Zap, Bell, Monitor } from "lucide-react";
import AboutUs from "../components/AboutUs";

export default function Home() {
  const scrollToAdvantages = () => {
    const advantagesSection = document.getElementById("advantages");
    if (advantagesSection) {
      advantagesSection.scrollIntoView({ behavior: "smooth" });
    }
  };

  const advantages = [
    {
      icon: <Calendar className="w-12 h-12 text-blue-600" />,
      title: "Smart Booking Management",
      description: "Easily view and manage all upcoming bookings for your station in one place. Confirm, cancel, or mark sessions as complete with just a click."
    },
    {
      icon: <Search className="w-12 h-12 text-blue-600" />,
      title: "QR Code Verification",
      description: "Quickly verify bookings using QR code scanning. No paperwork, no delays — just fast and reliable confirmation."
    },
    {
      icon: <Zap className="w-12 h-12 text-blue-600" />,
      title: "Real-Time Slot Availability",
      description: "Update slot availability instantly. Ensure your customers always know when a charger is free or in use."
    },
    {
      icon: <Bell className="w-12 h-12 text-blue-600" />,
      title: "Notifications & Alerts",
      description: "Stay informed with real-time updates about cancellations, completed sessions, or upcoming bookings."
    },
    {
      icon: <Monitor className="w-12 h-12 text-blue-600" />,
      title: "User-Friendly Dashboard",
      description: "Enjoy a clean and simple dashboard designed specifically for station operators, helping you focus on running your station smoothly."
    }
  ];

  return (
    <div>
      {/* Hero Section */}
      <div
        id="home"
        className="min-h-screen bg-cover bg-center bg-no-repeat relative"
        style={{ backgroundImage: `url(${bgImage})` }}
      >
        <div className="absolute inset-0 bg-black/40"></div>
        <div className="relative z-10 min-h-screen flex items-center">
          <div className="mx-auto max-w-7xl w-full px-4 sm:px-6 lg:px-8">
            <div className="grid grid-rows-1 lg:grid-rows-2 gap-12 lg:gap-20">
              {/* Heading */}
              <div className="flex items-center">
                <div>
                  <h1 className="text-5xl sm:text-6xl md:text-7xl font-bold text-white leading-tight">
                    EV Charging
                  </h1>
                  <h1 className="text-5xl sm:text-6xl md:text-7xl font-bold text-white leading-tight">
                    Solution For You
                  </h1>
                </div>
              </div>
              {/* Description + Button */}
              <div className="flex flex-col lg:flex-row justify-between items-start lg:items-end gap-8">
                <div className="flex flex-col">
                  <p className="text-xl sm:text-2xl md:text-3xl text-white/90 leading-relaxed">
                    Choosing the right EV charging
                  </p>
                  <p className="text-xl sm:text-2xl md:text-3xl text-white/90 leading-relaxed">
                    solution depends on your needs lifestyle.
                  </p>
                </div>
                <button
                  onClick={scrollToAdvantages}
                  className="flex items-center gap-1 text-white text-sm sm:text-lg font-medium transition-all duration-300 group cursor-pointer hover:gap-2"
                >
                  <span className="drop-shadow-lg">Explore More</span>
                  <ArrowDown className="w-6 h-6 group-hover:translate-y-1 transition-transform" />
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Advantages Section */}
      <section id="advantages" className="py-16 sm:py-20 lg:py-24 bg-gradient-to-b from-gray-50 to-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          {/* Hero Section */}
          <div className="text-center mb-16">
            <h2 className="text-4xl sm:text-5xl md:text-6xl font-bold text-gray-900 mb-6">
              Why EV Operators Love Our Platform
            </h2>
            <p className="text-lg sm:text-xl text-gray-600 max-w-3xl mx-auto">
              Discover how we make managing your charging station simple, efficient, and stress-free.
            </p>
          </div>

          {/* Intro Paragraph */}
          <div className="mb-16 bg-white rounded-2xl shadow-lg p-8 sm:p-10">
            <h3 className="text-2xl sm:text-3xl font-bold text-gray-900 mb-4">
              Empowering EV Operators for a Smarter Future
            </h3>
            <p className="text-base sm:text-lg text-gray-700 leading-relaxed">
              Managing a charging station has never been easier. Our platform equips EV operators with everything they need — from real-time booking updates to effortless QR code verification. With a powerful yet simple dashboard, operators can focus less on admin work and more on delivering reliable charging services.
            </p>
          </div>

          {/* Advantages Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 lg:gap-8 mb-16">
            {advantages.map((advantage, index) => (
              <div
                key={index}
                className="bg-white rounded-xl shadow-lg p-6 sm:p-8 hover:shadow-2xl transition-all duration-300 transform hover:-translate-y-2"
              >
                <div className="mb-4">
                  {advantage.icon}
                </div>
                <h4 className="text-xl font-bold text-gray-900 mb-3">
                  {advantage.title}
                </h4>
                <p className="text-gray-600 leading-relaxed">
                  {advantage.description}
                </p>
              </div>
            ))}
          </div>

          {/* Testimonial Section */}
          <div className="bg-blue-600 rounded-2xl shadow-xl p-8 sm:p-12 mb-16 text-center">
            <div className="max-w-3xl mx-auto">
              <svg
                className="w-12 h-12 text-blue-200 mx-auto mb-6"
                fill="currentColor"
                viewBox="0 0 24 24"
              >
                <path d="M14.017 21v-7.391c0-5.704 3.731-9.57 8.983-10.609l.995 2.151c-2.432.917-3.995 3.638-3.995 5.849h4v10h-9.983zm-14.017 0v-7.391c0-5.704 3.748-9.57 9-10.609l.996 2.151c-2.433.917-3.996 3.638-3.996 5.849h3.983v10h-9.983z" />
              </svg>
              <p className="text-white text-lg sm:text-xl italic mb-6 leading-relaxed">
                "This platform has made my daily work so much easier. I can confirm bookings in seconds and keep track of everything hassle-free."
              </p>
              <p className="text-blue-100 font-semibold">
                — Sarah Johnson, Station Operator
              </p>
            </div>
          </div>

          {/* Call to Action */}
          <div className="text-center bg-gradient-to-r from-blue-600 to-indigo-600 rounded-2xl shadow-2xl p-10 sm:p-16">
            <h3 className="text-3xl sm:text-4xl md:text-5xl font-bold text-white mb-6">
              Ready to simplify your station management?
            </h3>
            <button className="bg-white text-blue-600 px-8 sm:px-12 py-3 sm:py-4 rounded-full text-lg font-semibold shadow-lg hover:shadow-xl transition-all duration-300 transform hover:scale-105 cursor-pointer">
              Get Started
            </button>
          </div>
        </div>
      </section>

    </div>
  );
}