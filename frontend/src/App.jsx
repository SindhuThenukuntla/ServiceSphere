import { BrowserRouter, Routes, Route, useLocation } from "react-router-dom";

import Sidebar from "./components/Sidebar";
import Header from "./components/Header";

import Dashboard from "./pages/Dashboard";
import SlaManagement from "./pages/SlaManagement";
import EscalationManagement from "./pages/EscalationManagement";
import Notifications from "./pages/Notifications";
import SupportQueues from "./pages/SupportQueues";

import "./App.css";

function AppLayout() {
  const location = useLocation();

  const pageTitles = {
    "/": "Dashboard",
    "/sla": "SLA Management",
    "/escalations": "Escalations",
    "/notifications": "Notifications",
    "/queues": "Support Queues",
  };

  const activePage = pageTitles[location.pathname] || "Dashboard";

  return (
    <div className="app-layout">
      <Sidebar />

      <main className="main-content">
        <Header activePage={activePage} />

        <div className="page-container">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/sla" element={<SlaManagement />} />
            <Route path="/escalations" element={<EscalationManagement />} />
            <Route path="/notifications" element={<Notifications />} />
            <Route path="/queues" element={<SupportQueues />} />
            <Route path="*" element={<h2>404 - Page Not Found</h2>} />
          </Routes>
        </div>
      </main>
    </div>
  );
}

function App() {
  return (
    <BrowserRouter>
      <AppLayout />
    </BrowserRouter>
  );
}

export default App;
