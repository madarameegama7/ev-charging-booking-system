import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import SignUp from "./pages/SignUp";
import Home from "./pages/Home";
import Dashboard from "./pages/admin/Dashboard";
import ProtectedRoute from "./components/ProtectedRoute";
import RoleRoute from "./components/RoleRoute";
import OperatorDashboard from "./pages/operator/Dashboard";
import Layout from "./components/Layout";
import AboutUs from "./components/AboutUs";
import ContactUs from "./components/ContactUS";
import Profile from "./pages/Profile";

export default function App() {
  return (
    <Router>
      <Routes>
        <Route
          path="/"
          element={
            <Layout>
              <Home />
            </Layout>
          }
        />

        <Route
          path="/aboutus"
          element={
            <Layout>
              <AboutUs />
            </Layout>
          }
        />

        <Route
          path="/contactus"
          element={
            <Layout>
              <ContactUs />
            </Layout>
          }
        />

        <Route path="/profile" element={<Profile />} />

        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignUp />} />
        <Route
          path="/admin/dashboard"
          element={
            <RoleRoute roles={["Backoffice"]}>
              <Dashboard />
            </RoleRoute>
          }
        />
        <Route
          path="/operator/dashboard"
          element={
            <RoleRoute roles={["Operator"]}>
              <OperatorDashboard />
            </RoleRoute>
          }
        />
      </Routes>
    </Router>
  );
}
