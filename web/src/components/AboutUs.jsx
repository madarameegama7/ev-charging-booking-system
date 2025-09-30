import image1 from "../assets/home/home3.jpg";
import { Target, Eye, Sparkles, Heart, Users, Leaf } from "lucide-react";

export default function AboutUs() {
  return (
    <div id="about-us" className="min-h-screen relative overflow-hidden">
      {/* Decorative Background Elements */}
      <div className="absolute top-0 right-0 w-96 h-96 bg-[#347928]/5 rounded-full blur-3xl"></div>
      <div className="absolute bottom-0 left-0 w-96 h-96 bg-emerald-200/20 rounded-full blur-3xl"></div>

      <div className="relative z-10 py-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col gap-20">
          {/* Page Header */}
          <div className="text-center mt-10">
            <h1 className="text-5xl md:text-6xl lg:text-7xl font-semibold text-gray-900">
              Driving the <span className="text-[#347928]">Future</span> of
              Mobility
            </h1>
          </div>

          {/* Hero Section - Image and Text */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
            {/* Left: Image with Modern Frame */}
            <div className="relative group">
              {/* Decorative Background */}
              <div className="absolute inset-0 bg-gradient-to-br from-[#347928] to-emerald-600 rounded-3xl transform rotate-3 opacity-20 group-hover:rotate-6 transition-transform duration-500"></div>

              {/* Main Image Container */}
              <div className="relative bg-white p-3 rounded-3xl shadow-2xl transform group-hover:scale-105 transition-transform duration-500">
                <img
                  src={image1}
                  alt="EV Charging Station"
                  className="w-full h-80 lg:h-96 object-cover rounded-2xl"
                />
              </div>
            </div>

            {/* Right: Text Content with Modern Design */}
            <div className="flex flex-col gap-6">
              <h2 className="text-4xl md:text-5xl font-semibold text-gray-900 leading-tight">
                Empowering <span className="text-[#347928]">EV Operators</span>
              </h2>

              <p className="text-lg md:text-xl text-gray-800 leading-relaxed">
                We believe station operators are at the heart of the EV charging
                ecosystem. Our platform gives them the tools to manage bookings,
                track availability, and deliver reliable charging services with
                ease.
              </p>

              {/* Feature Pills */}
              <div className="flex flex-wrap gap-3 pt-4">
                <div className="px-4 py-2 bg-[#347928]/10 rounded-full text-sm font-semibold text-[#347928]">
                  Smart Management
                </div>
                <div className="px-4 py-2 bg-[#347928]/10 rounded-full text-sm font-semibold text-[#347928]">
                  Real-Time Updates
                </div>
                <div className="px-4 py-2 bg-[#347928]/10 rounded-full text-sm font-semibold text-[#347928]">
                  24/7 Support
                </div>
              </div>
            </div>
          </div>

          {/* Our Story Section - Modern Card Design */}
          <div className="relative">
            {/* Decorative Corner Elements */}
            <div className="absolute top-0 left-0 w-32 h-32 bg-[#347928]/10 rounded-xl"></div>

            <div className="relative bg-[#347928] rounded-[3rem] p-8 md:p-12 lg:p-16 shadow-2xl overflow-hidden">
              {/* Decorative Circles */}
              <div className="relative z-10 max-w-4xl mx-auto space-y-8">
                <div className="text-center">
                  <h2 className="text-4xl md:text-5xl lg:text-6xl  text-white">
                    Our Story
                  </h2>
                </div>

                <p className="text-lg md:text-xl text-white/95 leading-relaxed text-center">
                  Our journey began with a simple idea to make electric vehicle
                  charging smarter and more accessible. We built this platform
                  to help EV owners find and book charging slots with ease,
                  while empowering station operators to manage their operations
                  efficiently. With a focus on innovation and sustainability, we
                  are driving the future of clean energy mobility.
                </p>

                {/* Timeline Stats */}
                <div className="grid grid-cols-3 gap-6 pt-8">
                  <div className="text-center">
                    <div className="text-4xl font-black text-white">2025</div>
                    <div className="text-sm text-white/80">Founded</div>
                  </div>
                  <div className="text-center">
                    <div className="text-4xl font-black text-white">500+</div>
                    <div className="text-sm text-white/80">Stations</div>
                  </div>
                  <div className="text-center">
                    <div className="text-4xl font-black text-white">10K+</div>
                    <div className="text-sm text-white/80">Happy Users</div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="w-full px-4 py-8 sm:px-6 lg:px-8">
            {/* Vision and Mission Section */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 sm:gap-6 lg:gap-8 max-w-7xl mx-auto">
              {/* Vision Card */}
              <div className="flex flex-col gap-3 sm:gap-4 px-4 py-6 sm:px-6 sm:py-8 lg:px-8 lg:py-10 group relative bg-[#347928]/15 rounded-xl sm:rounded-2xl border border-gray-200 hover:border-[#347928]/50 transition-all duration-300">
                {/* Icon */}
                <div className="flex ">
                  <Eye className="w-6 h-6 sm:w-7 sm:h-7 lg:w-8 lg:h-8 text-[#347928]" />
                </div>

                <h3 className="text-2xl sm:text-3xl lg:text-4xl font-black text-gray-900">
                  Our Vision
                </h3>

                <p className="text-base sm:text-lg leading-relaxed text-gray-800">
                  To become the leading platform that connects EV owners and
                  charging stations, enabling a seamless, sustainable, and
                  eco-friendly driving experience.
                </p>
              </div>

              {/* Mission Card */}
              <div className="flex flex-col gap-3 sm:gap-4 px-4 py-6 sm:px-6 sm:py-8 lg:px-8 lg:py-10 group relative bg-[#347928]/15 rounded-xl sm:rounded-2xl border border-gray-200 hover:border-[#347928]/50 transition-all duration-300">
                {/* Icon */}
                <div className="flex">
                  <Target className="w-6 h-6 sm:w-7 sm:h-7 lg:w-8 lg:h-8 text-[#347928]" />
                </div>

                <h3 className="text-2xl sm:text-3xl lg:text-4xl font-black text-gray-900">
                  Our Mission
                </h3>

                <p className="text-base sm:text-lg leading-relaxed text-gray-800">
                  Our mission is to simplify EV charging through smart booking,
                  reliable station management, and real-time connectivity. We
                  strive to empower EV operators with efficient tools and
                  provide EV owners with convenience, confidence, and a greener
                  tomorrow.
                </p>
              </div>
            </div>
          </div>

          {/* Core Values Section */}
          <div className="flex flex-col gap-6 text-center">
            <h2 className="text-4xl md:text-5xl font-semibold text-gray-900">
              Our <span className="text-[#347928]">Core Values</span>
            </h2>

            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
              {[
                {
                  icon: "🌱",
                  title: "Sustainability",
                  desc: "Eco-friendly solutions",
                },
                {
                  icon: "⚡",
                  title: "Innovation",
                  desc: "Cutting-edge technology",
                },
                {
                  icon: "🤝",
                  title: "Reliability",
                  desc: "Always there for you",
                },
                { icon: "💚", title: "Community", desc: "Growing together" },
              ].map((value, i) => (
                <div
                  key={i}
                  className="p-6 bg-[#347928]/5 rounded-3xl hover:bg-[#347928]/10 transition-colors duration-300"
                >
                  <div className="text-4xl mb-3">{value.icon}</div>
                  <h4 className="text-xl font-bold text-gray-900 mb-2">
                    {value.title}
                  </h4>
                  <p className="text-gray-600">{value.desc}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
