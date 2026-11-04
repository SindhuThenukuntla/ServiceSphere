import React, { useState, useEffect } from 'react';
import Navbar from './components/Navbar';
import AssetList from './components/AssetList';
import AssetDetailModal from './components/AssetDetailModal';
import AssignAssetModal from './components/AssignAssetModal';
import ReturnAssetModal from './components/ReturnAssetModal';
import CreateAssetModal from './components/CreateAssetModal';
import MyAssetsView from './components/MyAssetsView';
import TicketAssetLinkView from './components/TicketAssetLinkView';
import { fetchAssets, fetchAssetSummary, fetchUsers } from './services/api';
import './App.css';

export default function App() {
  const [activeTab, setActiveTab] = useState('inventory');
  const [assets, setAssets] = useState([]);
  const [summary, setSummary] = useState({});
  const [users, setUsers] = useState([]);
  const [currentUser, setCurrentUser] = useState({
    id: 1,
    fullName: 'Marcus Vance',
    email: 'marcus.vance@servicesphere.local',
    role: 'IT Asset Manager',
    department: 'IT Operations'
  });

  const [search, setSearch] = useState('');
  const [selectedType, setSelectedType] = useState('');
  const [selectedStatus, setSelectedStatus] = useState('');

  // Modals
  const [detailAsset, setDetailAsset] = useState(null);
  const [assigningAsset, setAssigningAsset] = useState(null);
  const [returningAsset, setReturningAsset] = useState(null);
  const [showCreateModal, setShowCreateModal] = useState(false);

  // Notification toast
  const [notification, setNotification] = useState('');

  useEffect(() => {
    loadAll();
  }, [search, selectedType, selectedStatus]);

  const loadAll = async () => {
    try {
      const [assetData, summaryData, userData] = await Promise.all([
        fetchAssets(selectedType, selectedStatus, search),
        fetchAssetSummary(),
        fetchUsers()
      ]);
      setAssets(assetData.content || []);
      setSummary(summaryData || {});
      setUsers(userData || []);

      if (userData && userData.length > 0 && !currentUser.fullName) {
        setCurrentUser(userData[0]);
      }
    } catch (err) {
      console.error('Error loading data:', err);
    }
  };

  const handleRoleChange = (newRole) => {
    const userForRole = users.find(u => u.role === newRole) || users[0];
    if (userForRole) {
      setCurrentUser(userForRole);
      showToast(`Switched persona to ${userForRole.fullName} (${userForRole.role})`);
    }
  };

  const showToast = (msg) => {
    setNotification(msg);
    setTimeout(() => {
      setNotification('');
    }, 4500);
  };

  const canManageAssets = currentUser.role === 'IT Asset Manager' || currentUser.role === 'Administrator';

  return (
    <div className="servicesphere-app">
      {/* Toast Notification */}
      {notification && (
        <div className="toast-notification">
          <span className="toast-icon">✓</span>
          <span className="toast-msg">{notification}</span>
          <button className="toast-close" onClick={() => setNotification('')}>✕</button>
        </div>
      )}

      {/* Main Navbar */}
      <Navbar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        currentRole={currentUser.role}
        setCurrentRole={handleRoleChange}
        currentUser={currentUser}
        users={users}
      />

      {/* Main Content Area */}
      <main className="main-content">
        {activeTab === 'inventory' && (
          <AssetList
            assets={assets}
            summary={summary}
            search={search}
            setSearch={setSearch}
            selectedType={selectedType}
            setSelectedType={setSelectedType}
            selectedStatus={selectedStatus}
            setSelectedStatus={setSelectedStatus}
            onOpenDetails={asset => setDetailAsset(asset)}
            onOpenAssign={asset => setAssigningAsset(asset)}
            onOpenReturn={asset => setReturningAsset(asset)}
            onOpenCreate={() => setShowCreateModal(true)}
            canManageAssets={canManageAssets}
          />
        )}

        {activeTab === 'my-assets' && (
          <MyAssetsView
            currentUser={currentUser}
            assets={assets}
            onOpenDetails={asset => setDetailAsset(asset)}
            onReportIssue={(asset) => {
              setActiveTab('ticket-linking');
              showToast(`Linking asset ${asset.assetTag} to ticket workflow.`);
            }}
          />
        )}

        {activeTab === 'ticket-linking' && (
          <TicketAssetLinkView
            assets={assets}
            users={users}
            onRefresh={loadAll}
          />
        )}
      </main>

      {/* Modals */}
      {detailAsset && (
        <AssetDetailModal
          asset={detailAsset}
          onClose={() => setDetailAsset(null)}
          onRefresh={loadAll}
          onOpenAssign={asset => setAssigningAsset(asset)}
          onOpenReturn={asset => setReturningAsset(asset)}
          canManageAssets={canManageAssets}
        />
      )}

      {assigningAsset && (
        <AssignAssetModal
          asset={assigningAsset}
          users={users.filter(u => u.status === 'ACTIVE')}
          currentUser={currentUser}
          onClose={() => setAssigningAsset(null)}
          onSuccess={(msg) => {
            showToast(msg);
            loadAll();
          }}
        />
      )}

      {returningAsset && (
        <ReturnAssetModal
          asset={returningAsset}
          onClose={() => setReturningAsset(null)}
          onSuccess={(msg) => {
            showToast(msg);
            loadAll();
          }}
        />
      )}

      {showCreateModal && (
        <CreateAssetModal
          onClose={() => setShowCreateModal(false)}
          onSuccess={(msg) => {
            showToast(msg);
            loadAll();
          }}
        />
      )}

      {/* Footer */}
      <footer className="app-footer">
        <div>
          <strong>ServiceSphere ITSM</strong> • Student 3: Asset Inventory, Assignment/Return & Ticket Linking
        </div>
        <div className="footer-links">
          <span>Tech Stack: React 19 + Spring Boot 3 + MySQL 8</span>
          <span>•</span>
          <span>Integrity & Atomic Transactions Verified</span>
        </div>
      </footer>
    </div>
  );
}
