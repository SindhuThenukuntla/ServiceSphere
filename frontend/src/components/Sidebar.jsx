
import { NavLink } from "react-router-dom";

function Sidebar() {
  const menuItems = [
    { name: "Dashboard", path: "/", icon: "🏠" },
    { name: "SLA Management", path: "/sla", icon: "⏱️" },
    { name: "Escalations", path: "/escalations", icon: "🚨" },
    { name: "Notifications", path: "/notifications", icon: "🔔" },
    { name: "Support Queues", path: "/queues", icon: "📋" },
  ];

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <h2>ServiceSphere</h2>
        <p>IT Service Management</p>
      </div>

      <nav className="sidebar-menu">
        <p className="menu-heading">WORKSPACE</p>

        {menuItems.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            end={item.path === "/"}
            className={({ isActive }) =>
              `menu-item ${isActive ? "active" : ""}`
            }
          >
            <span>{item.icon}</span>
            {item.name}
          </NavLink>
        ))}
      </nav>

      <div className="sidebar-footer">
        <div className="profile-avatar">S4</div>
        <div>
          <strong>Support Team</strong>
          <p>Student 4</p>
        </div>
      </div>
    </aside>
  );
}

export default Sidebar;
