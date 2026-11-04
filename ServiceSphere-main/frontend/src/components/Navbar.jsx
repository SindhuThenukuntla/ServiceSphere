import React from 'react';

export default function Navbar({ activeTab, setActiveTab, currentRole, setCurrentRole, currentUser, users }) {
  return (
    <header className="navbar-container">
      <div className="navbar-top">
        <div className="brand-section">
          <div className="brand-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path>
              <polyline points="3.27 6.96 12 12.01 20.73 6.96"></polyline>
              <line x1="12" y1="22.08" x2="12" y2="12"></line>
            </svg>
          </div>
          <div>
            <div className="brand-title">ServiceSphere <span className="badge-itsm">ITSM Enterprise</span></div>
            <div className="brand-subtitle">Student 3: Asset Inventory, Assignment/Return, History & Ticket Linking</div>
          </div>
        </div>

        <div className="user-role-section">
          <div className="role-selector-box">
            <label htmlFor="role-select">Current Persona / Role:</label>
            <select
              id="role-select"
              value={currentUser.id}
              onChange={(e) => {
                const selected = users.find(u => u.id === Number(e.target.value));
                if (selected) {
                  setCurrentRole(selected.role);
                }
              }}
              className="role-select"
            >
              {users.map(u => (
                <option key={u.id} value={u.id}>
                  {u.fullName} — {u.role} ({u.department || 'All'})
                </option>
              ))}
            </select>
          </div>

          <div className="user-profile-badge">
            <div className="avatar-circle">
              {currentUser.fullName ? currentUser.fullName.charAt(0) : 'U'}
            </div>
            <div className="user-meta">
              <div className="user-name">{currentUser.fullName}</div>
              <div className="user-role-tag">{currentUser.role}</div>
            </div>
          </div>
        </div>
      </div>

      <nav className="nav-tabs">
        <button
          className={`nav-tab ${activeTab === 'inventory' ? 'active' : ''}`}
          onClick={() => setActiveTab('inventory')}
        >
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <rect x="2" y="3" width="20" height="14" rx="2" ry="2"></rect>
            <line x1="8" y1="21" x2="16" y2="21"></line>
            <line x1="12" y1="17" x2="12" y2="21"></line>
          </svg>
          Asset Inventory
        </button>

        <button
          className={`nav-tab ${activeTab === 'my-assets' ? 'active' : ''}`}
          onClick={() => setActiveTab('my-assets')}
        >
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
            <circle cx="12" cy="7" r="4"></circle>
          </svg>
          My Assigned Assets
        </button>

        <button
          className={`nav-tab ${activeTab === 'ticket-linking' ? 'active' : ''}`}
          onClick={() => setActiveTab('ticket-linking')}
        >
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"></path>
            <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"></path>
          </svg>
          Ticket-Asset Linking & Integrity
        </button>
      </nav>
    </header>
  );
}

