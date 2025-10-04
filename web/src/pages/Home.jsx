import {
  ArrowDown,
  Calendar,
  Search,
  Zap,
  Bell,
  Monitor,
  ChevronRight,
} from "lucide-react";
import homeImage from "../assets/home/evstation2.jpg";

export default function Home() {
  const scrollToAdvantages = () => {
    const advantagesSection = document.getElementById("advantages");
    if (advantagesSection) {
      advantagesSection.scrollIntoView({ behavior: "smooth" });
    }
  };

  const advantages = [
    {
      icon: <Calendar className="w-8 h-8 sm:w-10 sm:h-10 text-[#347928]" />,
      title: "Smart Booking Management",
      description:
        "Easily view and manage all upcoming bookings for your station in one place. Confirm, cancel, or mark sessions as complete with just a click.",
    },
    {
      icon: <Search className="w-8 h-8 sm:w-10 sm:h-10 text-[#347928]" />,
      title: "QR Code Verification",
      description:
        "Quickly verify bookings using QR code scanning. No paperwork, no delays — just fast and reliable confirmation.",
    },
    {
      icon: <Zap className="w-8 h-8 sm:w-10 sm:h-10 text-[#347928]" />,
      title: "Real-Time Slot Availability",
      description:
        "Update slot availability instantly. Ensure your customers always know when a charger is free or in use.",
    },
    {
      icon: <Bell className="w-8 h-8 sm:w-10 sm:h-10 text-[#347928]" />,
      title: "Notifications & Alerts",
      description:
        "Stay informed with real-time updates about cancellations, completed sessions, or upcoming bookings.",
    },
    {
      icon: <Monitor className="w-8 h-8 sm:w-10 sm:h-10 text-[#347928]" />,
      title: "User-Friendly Dashboard",
      description:
        "Enjoy a clean and simple dashboard designed specifically for station operators, helping you focus on running your station smoothly.",
    },
  ];

  return (
    <div className="overflow-hidden">
      {/* Hero Section */}
      <div
        id="home"
        className="relative min-h-screen bg-gradient-to-br from-emerald-50 via-white to-green-50"
      >
        {/* Decorative Elements */}
        <div className="hidden sm:block absolute top-20 right-10 w-72 h-72 bg-[#347928]/10 rounded-full blur-3xl"></div>
        <div className="hidden sm:block absolute bottom-20 left-10 w-96 h-96 bg-emerald-200/20 rounded-full blur-3xl"></div>

        <div className="relative z-10 min-h-screen flex items-center px-4 sm:px-6 lg:px-8">
          <div className="w-full max-w-7xl mx-auto">
            <div className="flex flex-col lg:flex-row gap-8 sm:gap-10 md:gap-12 lg:gap-16 items-center">
              <div className="flex-1 flex flex-col gap-6 sm:gap-8 text-center lg:text-left">
                <h1 className="text-4xl sm:text-5xl md:text-6xl lg:text-7xl font-semibold text-gray-900 leading-tight tracking-tight">
                  EV Charging
                  <span className="block bg-gradient-to-r from-[#347928] to-emerald-600 bg-clip-text text-transparent">
                    Made Simple
                  </span>
                </h1>

                <p className="text-base sm:text-lg md:text-xl lg:text-2xl text-gray-800 leading-relaxed max-w-xl mx-auto lg:mx-0">
                  The smart platform that connects EV owners with charging
                  stations seamlessly. Book, charge, and go.
                </p>

                <div className="flex flex-col sm:flex-row gap-3 sm:gap-4 justify-center lg:justify-start w-full sm:w-auto">
                  <button
                    onClick={scrollToAdvantages}
                    className="group flex items-center justify-center gap-2 py-3 px-6 sm:py-4 sm:px-8 bg-[#347928] text-white rounded-full font-semibold text-sm sm:text-base md:text-lg shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105 w-full sm:w-auto"
                  >
                    Explore Features
                    <ChevronRight className="w-4 h-4 sm:w-5 sm:h-5 group-hover:translate-x-1 transition-transform" />
                  </button>
                  <button className="bg-white text-[#347928] border-2 border-[#347928] py-3 px-6 sm:py-4 sm:px-8 rounded-full font-semibold text-sm sm:text-base md:text-lg hover:bg-[#347928]/5 transition-all duration-300 cursor-pointer w-full sm:w-auto">
                    Get Started Free
                  </button>
                </div>

                <div className="flex gap-4 sm:gap-8 md:gap-12 justify-center lg:justify-start max-w-lg mx-auto lg:mx-0">
                  <div className="flex flex-col gap-1">
                    <div className="text-2xl sm:text-3xl font-bold text-[#347928]">
                      500+
                    </div>
                    <div className="text-xs sm:text-sm text-gray-600">
                      Stations
                    </div>
                  </div>
                  <div className="flex flex-col gap-1">
                    <div className="text-2xl sm:text-3xl font-bold text-[#347928]">
                      10K+
                    </div>
                    <div className="text-xs sm:text-sm text-gray-600">
                      Users
                    </div>
                  </div>
                  <div className="flex flex-col gap-1">
                    <div className="text-2xl sm:text-3xl font-bold text-[#347928]">
                      24/7
                    </div>
                    <div className="text-xs sm:text-sm text-gray-600">
                      Support
                    </div>
                  </div>
                </div>
              </div>

              {/* Right Side - Image */}
              <div className="flex-1 flex items-center justify-center lg:justify-end w-full">
                <div className="relative w-full max-w-sm sm:max-w-md lg:max-w-lg">
                  {/* Decorative Background */}
                  <div className="absolute inset-0 bg-gradient-to-br from-[#347928] to-emerald-600 rounded-3xl transform rotate-3 opacity-20"></div>

                  {/* Main Image Card */}
                  <div className="relative bg-white rounded-3xl shadow-2xl transform hover:scale-105 transition-transform duration-500 p-2 sm:p-3">
                    <img
                      src={homeImage}
                      alt="EV Charging Station"
                      className="w-full h-auto rounded-3xl object-cover"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Scroll Indicator */}
        <div className="hidden sm:block absolute bottom-8 left-1/2 transform -translate-x-1/2 animate-bounce">
          <ArrowDown className="w-6 h-6 text-[#347928]" />
        </div>
      </div>

      {/* Features Section */}
      <section
        id="advantages"
        className="relative bg-white py-12 sm:py-16 md:py-20"
      >
        <div className="w-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex flex-col gap-12 sm:gap-16 md:gap-20">
            <div className="flex flex-col gap-4 sm:gap-6 items-center text-center">
              <div className="bg-[#347928]/10 rounded-full px-4 py-2">
                <span className="text-xs sm:text-sm font-semibold text-[#347928]">
                  FOR OPERATORS
                </span>
              </div>
              <h2 className="text-3xl sm:text-4xl md:text-5xl lg:text-6xl font-semibold text-gray-900">
                Why <span className="text-[#347928]">EV Operators</span> Choose
                Us
              </h2>
              <p className="text-base sm:text-lg md:text-xl text-gray-800 max-w-3xl">
                Powerful tools designed to make station management effortless
                and efficient
              </p>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 sm:gap-8">
              {advantages.map((advantage, index) => (
                <div
                  key={index}
                  className="group relative bg-gradient-to-br from-gray-50 to-white rounded-2xl sm:rounded-3xl border border-gray-200 hover:border-[#347928]/50 hover:shadow-2xl transition-all duration-300 transform hover:-translate-y-2 p-6 sm:p-8"
                >
                  <div className="flex flex-col gap-4 sm:gap-6">
                    <div className="w-12 h-12 sm:w-16 sm:h-16 bg-[#347928]/10 rounded-xl sm:rounded-2xl flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                      {advantage.icon}
                    </div>

                    <div className="flex flex-col gap-2 sm:gap-3">
                      <h4 className="text-lg sm:text-xl font-bold text-gray-900">
                        {advantage.title}
                      </h4>
                      <p className="text-sm sm:text-base text-gray-600 leading-relaxed">
                        {advantage.description}
                      </p>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            {/* CTA Banner */}
            <div className="relative overflow-hidden bg-gradient-to-r from-[#347928] to-emerald-600 rounded-2xl sm:rounded-3xl p-8 sm:p-10 md:p-12">
              <div className="relative z-10 flex flex-col gap-6 sm:gap-8 items-center text-center">
                <h3 className="text-2xl sm:text-3xl md:text-4xl lg:text-5xl font-bold text-white">
                  Ready to Transform Your Station?
                </h3>
                <p className="text-base sm:text-lg md:text-xl text-white/90 max-w-2xl">
                  Join hundreds of operators who trust our platform for seamless
                  charging management
                </p>
                <button className="bg-white text-[#347928] cursor-pointer rounded-full font-bold text-base sm:text-lg px-8 py-3 sm:px-10 sm:py-4 shadow-xl hover:shadow-2xl transition-all duration-300 hover:scale-105">
                  Start Free Trial
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
