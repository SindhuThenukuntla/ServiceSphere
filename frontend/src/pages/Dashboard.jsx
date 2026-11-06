
function Dashboard() {
  const stats = [
    { title: "Total Open Tickets", value: "124", icon: "📂", color: "blue" },
    { title: "SLA At Risk", value: "18", icon: "⏱️", color: "orange" },
    { title: "SLA Breached", value: "07", icon: "🚨", color: "red" },
    { title: "Unassigned Tickets", value: "12", icon: "📋", color: "purple" },
  ];

  return (
    <div className="dashboard">
      <div className="welcome-section">
        <h2>Support Operations Overview</h2>
        <p>Monitor SLA performance, escalations and support team workload.</p>
      </div>

      <div className="stats-grid">
        {stats.map((stat) => (
          <div className="stat-card" key={stat.title}>
            <div className={`stat-icon ${stat.color}`}>
              {stat.icon}
            </div>
            <div>
              <p>{stat.title}</p>
              <h2>{stat.value}</h2>
            </div>
          </div>
        ))}
      </div>

      <div className="dashboard-grid">
        <div className="dashboard-card">
          <h3>SLA Performance</h3>
          <p>Current ticket SLA status</p>

          <div className="performance-item">
            <span>On Track</span>
            <strong>75%</strong>
          </div>
          <div className="progress-bar">
            <div className="progress-fill green" style={{ width: "75%" }}></div>
          </div>

          <div className="performance-item">
            <span>At Risk</span>
            <strong>18%</strong>
          </div>
          <div className="progress-bar">
            <div className="progress-fill orange" style={{ width: "18%" }}></div>
          </div>

          <div className="performance-item">
            <span>Breached</span>
            <strong>7%</strong>
          </div>
          <div className="progress-bar">
            <div className="progress-fill red" style={{ width: "7%" }}></div>
          </div>
        </div>

        <div className="dashboard-card">
          <h3>Agent Workload</h3>
          <p>Assigned open tickets per agent</p>

          <div className="agent-row">
            <span>Agent 1</span>
            <strong>12 Tickets</strong>
          </div>
          <div className="agent-row">
            <span>Agent 2</span>
            <strong>08 Tickets</strong>
          </div>
          <div className="agent-row">
            <span>Agent 3</span>
            <strong>15 Tickets</strong>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
