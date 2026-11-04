import React from 'react';

export default function MyAssetsView({ currentUser, assets, onOpenDetails, onReportIssue }) {
  // Find assets assigned to this user
  const myAssets = assets.filter(
    a => a.currentAssignment && a.currentAssignment.userId === currentUser.id
  );

  return (
    <div className="module-container">
      <div className="view-intro-banner">
        <div className="intro-icon">💻</div>
        <div>
          <h2>My Assigned Company Assets</h2>
          <p>
            Hardware equipment currently issued to <strong>{currentUser.fullName}</strong> ({currentUser.department || 'Employee'}).
            Review condition reports, warranty coverage, or submit a support ticket if maintenance is needed.
          </p>
        </div>
      </div>

      {myAssets.length === 0 ? (
        <div className="empty-state-box">
          <div className="empty-icon">📂</div>
          <h3>No Hardware Currently Assigned</h3>
          <p>You do not currently have any IT assets assigned to your employee account.</p>
          <div className="hint-tip">
            Switch persona to <strong>Sarah Chen</strong> (ThinkPad X1) or <strong>David Kim</strong> (Mac Studio) using the top-right persona selector to inspect employee assigned assets.
          </div>
        </div>
      ) : (
        <div className="my-assets-grid">
          {myAssets.map(asset => (
            <div key={asset.id} className="my-asset-card">
              <div className="card-header">
                <span className="asset-tag">{asset.assetTag}</span>
                <span className="asset-type-badge">{asset.assetType}</span>
                <span className="status-pill assigned">Assigned to You</span>
              </div>

              <div className="card-body">
                <h3 className="asset-title">{asset.brand} {asset.model}</h3>
                <div className="meta-list">
                  <div className="meta-row">
                    <span className="meta-k">Serial Number:</span>
                    <span className="meta-v font-mono">{asset.serialNumber || 'N/A'}</span>
                  </div>
                  <div className="meta-row">
                    <span className="meta-k">Location:</span>
                    <span className="meta-v">{asset.location || 'Assigned Desk'}</span>
                  </div>
                  <div className="meta-row">
                    <span className="meta-k">Assigned Date:</span>
                    <span className="meta-v">
                      {new Date(asset.currentAssignment.assignedAt).toLocaleDateString()}
                    </span>
                  </div>
                  <div className="meta-row">
                    <span className="meta-k">Condition Issued:</span>
                    <span className="meta-v">{asset.currentAssignment.conditionOut}</span>
                  </div>
                  <div className="meta-row">
                    <span className="meta-k">Warranty Coverage:</span>
                    <span className="meta-v">
                      {asset.warrantyExpired ? (
                        <span className="text-danger font-semibold">Expired ({asset.warrantyEndDate})</span>
                      ) : asset.warrantyExpiringSoon ? (
                        <span className="text-warning font-semibold">Expiring soon in {asset.daysUntilWarrantyExpiry}d</span>
                      ) : (
                        <span className="text-success font-semibold">Active through {asset.warrantyEndDate}</span>
                      )}
                    </span>
                  </div>
                </div>

                {asset.notes && (
                  <div className="asset-desc-box">
                    <strong>Specs:</strong> {asset.notes}
                  </div>
                )}
              </div>

              <div className="card-footer">
                <button
                  className="btn-secondary"
                  onClick={() => onOpenDetails(asset)}
                >
                  View Details & History
                </button>
                <button
                  className="btn-primary"
                  onClick={() => onReportIssue(asset)}
                >
                  Report Issue / Link Ticket
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

