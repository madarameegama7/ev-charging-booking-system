import bgImage from "../assets/home/home4.jpg";
import { ArrowDown } from "lucide-react";
import AboutUs from "../components/AboutUs";

export default function Home() {
  const scrollToAbout = () => {
    const aboutSection = document.getElementById("about-us");
    if (aboutSection) {
      aboutSection.scrollIntoView({ behavior: "smooth" });
    }
  };

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
          <div className="mx-auto max-w-7xl w-full">
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
                  onClick={scrollToAbout}
                  className="flex items-center gap-1 text-white text-sm sm:text-lg font-medium transition-all duration-300 group cursor-pointer"
                >
                  <span className="drop-shadow-lg">Explore</span>
                  <ArrowDown className="w-6 h-6" />
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* AboutUs Component */}
      <AboutUs />
    </div>
  );
}
