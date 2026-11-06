
function Header({ activePage }) {
  return (
    <header className="header">
      <div className="header-title">
        <h2>{activePage}</h2>
        <p>Welcome to ServiceSphere Support Workspace</p>
      </div>

      <div className="header-actions">
        <button className="header-icon" title="Notifications">
          🔔
          <span className="notification-dot"></span>
        </button>

        <div className="header-user">
          <div className="user-avatar">S4</div>
          <div>
            <strong>Support Agent</strong>
            <p>Student 4</p>
          </div>
        </div>
      </div>
    </header>
  );
}

export default Header;
