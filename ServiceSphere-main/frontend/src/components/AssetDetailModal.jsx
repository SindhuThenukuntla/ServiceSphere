import React, { useEffect, useState } from 'react';
import { fetchAssetHistory, fetchTicketsForAsset, unlinkAssetFromTicket } from '../services/api';

export default function AssetDetailModal({ asset, onClose, onRefresh, onOpenAssign, onOpenReturn, canManageAssets }) {
  const [history, setHistory] = useState([]);
  const [linkedTickets, setLinkedTickets] = useState([]);
  const [loadingHistory, setLoadingHistory] = useState(true);
  const [loadingTickets, setLoadingTickets] = useState(true);
  const [actionError, setActionError] = useState('');

  useEffect(() => {
    if (!asset) return;

    setLoadingHistory(true);
    fetchAssetHistory(asset.id)
      .then(data => setHistory(data))
      .catch(err => console.error(err))
      .finally(() => setLoadingHistory(false));

    setLoadingTickets(true);
    fetchTicketsForAsset(asset.id)
      .then(data => setLinkedTickets(data))
      .catch(err => console.error(err))
      .finally(() => setLoadingTickets(false));
  }, [asset]);

  if (!asset) return null;

  const handleUnlinkTicket = async (ticketId) => {
    try {
      setActionError('');
      await unlinkAssetFromTicket(ticketId);
      setLinkedTickets(prev => prev.filter(t => t.ticketId !== ticketId));
      onRefresh();
    } catch (err) {
      setActionError(err.message || 'Failed to unlink ticket');
    }
  };

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-dialog modal-lg" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <div className="modal-header-title">
            <span className="asset-tag-large">{asset.assetTag}</span>
            <h2>{asset.brand} {asset.model}</h2>
            <span className={`status-pill ${asset.status.toLowerCase().replace(/\s+/g, '-')}`}>
              {asset.status}
            </span>
          </div>
          <button className="btn-close" onClick={onClose}>✕</button>
        </div>

        <div className="modal-body">
          {actionError && (
            <div className="form-error-banner">{actionError}</div>
          )}

          {/* Warranty Warning Banner if applicable */}
          {asset.warrantyExpired && (
            <div className="modal-alert-box danger">
              <span className="icon">⚠️</span>
              <div>
                <strong>Warranty Expired:</strong> This asset's manufacturer warranty expired on {asset.warrantyEndDate}. Any repair work requires out-of-warranty budget authorization.
              </div>
            </div>
          )}
          {asset.warrantyExpiringSoon && (
            <div className="modal-alert-box warning">
              <span className="icon">⏳</span>
              <div>
                <strong>Warranty Expiring Soon:</strong> This asset's warranty expires in {asset.daysUntilWarrantyExpiry} days ({asset.warrantyEndDate}). Schedule preventive maintenance.
              </div>
            </div>
          )}

          {/* Asset Metadata Grid */}
          <div className="detail-section">
            <h3 className="section-title">Asset Specifications</h3>
            <div className="specs-grid">
              <div className="spec-item">
                <span className="spec-label">Asset Type</span>
                <span className="spec-val">{asset.assetType}</span>
              </div>
              <div className="spec-item">
                <span className="spec-label">Serial Number</span>
                <span className="spec-val font-mono">{asset.serialNumber || 'N/A'}</span>
              </div>
              <div className="spec-item">
                <span className="spec-label">Location / Room</span>
                <span className="spec-val">{asset.location || 'Unassigned Depot'}</span>
              </div>
              <div className="spec-item">
                <span className="spec-label">Purchase Date</span>
                <span className="spec-val">{asset.purchaseDate || 'N/A'}</span>
              </div>
              <div className="spec-item">
                <span className="spec-label">Warranty End Date</span>
                <span className="spec-val">{asset.warrantyEndDate || 'N/A'}</span>
              </div>
              <div className="spec-item">
                <span className="spec-label">Current Status</span>
                <span className="spec-val font-semibold">{asset.status}</span>
              </div>
            </div>

            {asset.notes && (
              <div className="asset-notes-box">
                <span className="notes-label">Asset Notes:</span>
                <p>{asset.notes}</p>
              </div>
            )}
          </div>

          {/* Current Assignment Card */}
          <div className="detail-section">
            <div className="section-header-flex">
              <h3 className="section-title">Current Assignment</h3>
              {canManageAssets && asset.status === 'In Stock' && (
                <button className="btn-sm btn-primary" onClick={() => { onClose(); onOpenAssign(asset); }}>
                  Assign Asset
                </button>
              )}
              {canManageAssets && asset.status === 'Assigned' && (
                <button className="btn-sm btn-danger" onClick={() => { onClose(); onOpenReturn(asset); }}>
                  Return Asset
                </button>
              )}
            </div>

            {asset.currentAssignment ? (
              <div className="current-assignment-card">
                <div className="assignment-user-avatar">
                  {asset.currentAssignment.userName.charAt(0)}
                </div>
                <div className="assignment-user-info">
                  <div className="user-name">{asset.currentAssignment.userName}</div>
                  <div className="user-email">{asset.currentAssignment.userEmail} ({asset.currentAssignment.userDepartment || 'Staff'})</div>
                  <div className="assigned-date">
                    Assigned on: {new Date(asset.currentAssignment.assignedAt).toLocaleString()} by {asset.currentAssignment.assignedByName || 'IT Asset Manager'}
                  </div>
                  <div className="condition-box">
                    <strong>Condition Out:</strong> {asset.currentAssignment.conditionOut}
                  </div>
                </div>
              </div>
            ) : (
              <div className="no-assignment-state">
                {asset.status === 'In Stock' && 'Currently In Stock and available for employee deployment.'}
                {asset.status === 'In Repair' && 'Currently In Repair at service center. Assignment locked.'}
                {asset.status === 'Retired' && 'Asset is Retired from active corporate inventory.'}
              </div>
            )}
          </div>

          {/* Assignment History Audit Trail */}
          <div className="detail-section">
            <h3 className="section-title">Assignment History & Audit Trail</h3>
            {loadingHistory ? (
              <div className="loading-spinner">Loading history...</div>
            ) : history.length === 0 ? (
              <div className="text-muted">No historical assignment records found.</div>
            ) : (
              <div className="history-timeline">
                {history.map(item => (
                  <div key={item.id} className={`timeline-entry ${item.active ? 'active-entry' : 'completed-entry'}`}>
                    <div className="timeline-marker"></div>
                    <div className="timeline-content">
                      <div className="timeline-header">
                        <span className="employee-name">{item.userName}</span>
                        <span className="badge-timeline">{item.active ? 'Current Active Assignment' : 'Returned & Closed'}</span>
                      </div>
                      <div className="timeline-meta">
                        <span>Assigned: {new Date(item.assignedAt).toLocaleDateString()}</span>
                        {item.returnedAt && (
                          <span> • Returned: {new Date(item.returnedAt).toLocaleDateString()}</span>
                        )}
                        <span> • Assigner: {item.assignedByName || 'IT Asset Manager'}</span>
                      </div>
                      <div className="timeline-conditions">
                        <div><strong>Condition Out:</strong> {item.conditionOut}</div>
                        {item.conditionIn && (
                          <div><strong>Condition In:</strong> {item.conditionIn}</div>
                        )}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Linked Tickets & Cross-Team Validation */}
          <div className="detail-section">
            <h3 className="section-title">Linked Tickets ({linkedTickets.length})</h3>
            {loadingTickets ? (
              <div className="loading-spinner">Loading tickets...</div>
            ) : linkedTickets.length === 0 ? (
              <div className="text-muted">No support tickets currently linked to this asset.</div>
            ) : (
              <div className="tickets-list">
                {linkedTickets.map(t => (
                  <div key={t.ticketId || t.id} className="linked-ticket-item">
                    <div className="ticket-item-left">
                      <span className="ticket-code">{t.ticketNumber}</span>
                      <span className="ticket-title">{t.ticketTitle || t.title}</span>
                      <span className={`priority-tag ${(t.ticketPriority || t.priority).toLowerCase()}`}>
                        {t.ticketPriority || t.priority}
                      </span>
                      <span className="ticket-status-pill">{t.ticketStatus || t.status}</span>
                    </div>
                    {canManageAssets && (
                      <button
                        className="btn-unlink-ticket"
                        onClick={() => handleUnlinkTicket(t.ticketId || t.id)}
                        title="Unlink asset from ticket"
                      >
                        Unlink
                      </button>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        <div className="modal-footer">
          <button className="btn-secondary" onClick={onClose}>Close</button>
        </div>
      </div>
    </div>
  );
}

