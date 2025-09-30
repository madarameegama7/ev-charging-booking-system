import image1 from "../assets/home/home3.jpg";
import { Target, Eye } from "lucide-react";

export default function AboutUs() {
  return (
    <div id="about-us" className="min-h-screen py-20">
      <div className="max-w-7xl mx-auto flex flex-col gap-10 mt-10">
        {/* Hero Section - Image and Text */}
        <div>
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 items-center max-w-6xl mx-auto">
            {/* Left: Image */}
            <div className="flex justify-center lg:justify-end">
              <div className="w-80 h-60 lg:w-96 lg:h-72">
                <img
                  src={image1}
                  alt="EV Charging Station"
                  className="w-full h-full object-cover rounded-lg shadow-lg"
                />
              </div>
            </div>

            {/* Right: Text Content */}
            <div className="flex flex-col gap-6 p-4 lg:p-8">
              <h2 className="text-3xl md:text-4xl lg:text-4xl font-bold text-slate-900 leading-tight">
                Empowering EV Operators
              </h2>
              <p className="text-base md:text-lg text-slate-700 leading-relaxed">
                We believe station operators are at the heart of the EV charging
                ecosystem. Our platform gives them the tools to manage bookings,
                track availability, and deliver reliable charging services with
                ease. By simplifying daily operations, we enable operators to
                focus on what matters most — powering a cleaner, smarter future
                for every EV on the road.
              </p>
            </div>
          </div>
        </div>

        {/* Our Story Section */}
        <div
          className="rounded-3xl p-8 md:p-12 lg:p-16 shadow-2xl"
          style={{ backgroundColor: "#FAF9F3" }}
        >
          <div className="max-w-4xl mx-auto space-y-6">
            <div className="text-center space-y-4">
              <h2 className="text-4xl md:text-5xl font-bold text-slate-900">
                Our Story
              </h2>
            </div>
            <div className="space-y-6 text-slate-700 text-lg leading-relaxed">
              <p>
                Our journey began with a simple idea — to make electric vehicle
                charging smarter and more accessible. We built this platform to
                help EV owners find and book charging slots with ease, while
                empowering station operators to manage their operations
                efficiently. With a focus on innovation and sustainability, we
                are driving the future of clean energy mobility.
              </p>
            </div>
          </div>
        </div>

        {/* Vision and Mission Section */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          {/* Vision */}
          <div
            className="rounded-3xl p-8 md:p-10 shadow-2xl text-black"
            style={{ backgroundColor: "#FAF9F3" }}
          >
            <div className="space-y-6">
              <div className="flex items-center gap-4">
                <div className="bg-white/20 p-4 rounded-2xl backdrop-blur-sm">
                  <Eye className="w-8 h-8" />
                </div>
                <h3 className="text-3xl md:text-4xl font-bold">Our Vision</h3>
              </div>

              <p className="text-lg leading-relaxed text-black">
                To become the leading platform that connects EV owners and
                charging stations, enabling a seamless, sustainable, and
                eco-friendly driving experience.
              </p>
            </div>
          </div>

          {/* Mission */}
          <div
            className="rounded-3xl p-8 md:p-10 shadow-2xl text-black"
            style={{ backgroundColor: "#FAF9F3" }}
          >
            <div className="space-y-6">
              <div className="flex items-center gap-4">
                <div className="bg-white/20 p-4 rounded-2xl backdrop-blur-sm">
                  <Target className="w-8 h-8" />
                </div>
                <h3 className="text-3xl md:text-4xl font-bold">Our Mission</h3>
              </div>

              <p className="text-lg leading-relaxed text-black">
                Our mission is to simplify EV charging through smart booking,
                reliable station management, and real-time connectivity. We
                strive to empower EV operators with efficient tools and provide
                EV owners with convenience, confidence, and a greener tomorrow.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
