import React, { useState } from 'react';

export default function AssetList({
  assets,
  summary,
  search,
  setSearch,
  selectedType,
  setSelectedType,
  selectedStatus,
  setSelectedStatus,
  onOpenDetails,
  onOpenAssign,
  onOpenReturn,
  onOpenCreate,
  canManageAssets
}) {
  const [warrantyFilter, setWarrantyFilter] = useState('all');

  const assetTypes = ['Laptop', 'Desktop', 'Monitor', 'Phone', 'Peripheral', 'Network Equipment'];
  const assetStatuses = ['In Stock', 'Assigned', 'In Repair', 'Retired'];

  const filteredAssets = assets.filter(a => {
    if (warrantyFilter === 'expiring') return a.warrantyExpiringSoon;
    if (warrantyFilter === 'expired') return a.warrantyExpired;
    return true;
  });

  return (
    <div className="module-container">
      {/* Metrics Banner */}
      <div className="metrics-grid">
        <div className="metric-card total" onClick={() => { setSelectedStatus(''); setWarrantyFilter('all'); }}>
          <div className="metric-val">{summary.totalAssets || 0}</div>
          <div className="metric-label">Total Assets</div>
        </div>
        <div className={`metric-card in-stock ${selectedStatus === 'In Stock' ? 'active-filter' : ''}`}
             onClick={() => { setSelectedStatus('In Stock'); setWarrantyFilter('all'); }}>
          <div className="metric-val">{summary.inStockCount || 0}</div>
          <div className="metric-label">In Stock (Available)</div>
        </div>
        <div className={`metric-card assigned ${selectedStatus === 'Assigned' ? 'active-filter' : ''}`}
             onClick={() => { setSelectedStatus('Assigned'); setWarrantyFilter('all'); }}>
          <div className="metric-val">{summary.assignedCount || 0}</div>
          <div className="metric-label">Assigned</div>
        </div>
        <div className={`metric-card in-repair ${selectedStatus === 'In Repair' ? 'active-filter' : ''}`}
             onClick={() => { setSelectedStatus('In Repair'); setWarrantyFilter('all'); }}>
          <div className="metric-val">{summary.inRepairCount || 0}</div>
          <div className="metric-label">In Repair</div>
        </div>
        <div className={`metric-card retired ${selectedStatus === 'Retired' ? 'active-filter' : ''}`}
             onClick={() => { setSelectedStatus('Retired'); setWarrantyFilter('all'); }}>
          <div className="metric-val">{summary.retiredCount || 0}</div>
          <div className="metric-label">Retired</div>
        </div>
        <div className={`metric-card warning ${warrantyFilter === 'expiring' ? 'active-filter' : ''}`}
             onClick={() => { setWarrantyFilter(warrantyFilter === 'expiring' ? 'all' : 'expiring'); setSelectedStatus(''); }}>
          <div className="metric-val">{summary.upcomingWarrantyExpiryCount || 0}</div>
          <div className="metric-label">Warranty Expiring Soon</div>
        </div>
        <div className={`metric-card danger ${warrantyFilter === 'expired' ? 'active-filter' : ''}`}
             onClick={() => { setWarrantyFilter(warrantyFilter === 'expired' ? 'all' : 'expired'); setSelectedStatus(''); }}>
          <div className="metric-val">{summary.expiredWarrantyCount || 0}</div>
          <div className="metric-label">Warranty Expired</div>
        </div>
      </div>

      {/* Warranty Alert Callout if any */}
      {(summary.upcomingWarrantyExpiryCount > 0 || summary.expiredWarrantyCount > 0) && (
        <div className="warranty-alert-banner">
          <div className="alert-icon">⚠️</div>
          <div className="alert-text">
            <strong>Warranty & Maintenance Alert:</strong>{' '}
            {summary.upcomingWarrantyExpiryCount > 0 && `${summary.upcomingWarrantyExpiryCount} asset(s) have warranty expiring within 30 days. `}
            {summary.expiredWarrantyCount > 0 && `${summary.expiredWarrantyCount} asset(s) have expired warranties requiring service contract renewal.`}
          </div>
          <button
            className="btn-link"
            onClick={() => setWarrantyFilter(warrantyFilter === 'all' ? 'expiring' : 'all')}
          >
            {warrantyFilter === 'all' ? 'View Impacted Assets' : 'Show All Assets'}
          </button>
        </div>
      )}

      {/* Toolbar & Filters */}
      <div className="toolbar-section">
        <div className="search-box">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <circle cx="11" cy="11" r="8"></circle>
            <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
          </svg>
          <input
            type="text"
            placeholder="Search by asset tag, brand, model, serial, or location..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
          {search && (
            <button className="btn-clear" onClick={() => setSearch('')}>✕</button>
          )}
        </div>

        <div className="filters-row">
          <select
            value={selectedType}
            onChange={(e) => setSelectedType(e.target.value)}
            className="filter-dropdown"
          >
            <option value="">All Asset Types</option>
            {assetTypes.map(t => (
              <option key={t} value={t}>{t}</option>
            ))}
          </select>

          <select
            value={selectedStatus}
            onChange={(e) => setSelectedStatus(e.target.value)}
            className="filter-dropdown"
          >
            <option value="">All Statuses</option>
            {assetStatuses.map(s => (
              <option key={s} value={s}>{s}</option>
            ))}
          </select>

          {(selectedType || selectedStatus || search || warrantyFilter !== 'all') && (
            <button
              className="btn-reset"
              onClick={() => {
                setSelectedType('');
                setSelectedStatus('');
                setSearch('');
                setWarrantyFilter('all');
              }}
            >
              Reset Filters
            </button>
          )}

          {canManageAssets && (
            <button className="btn-primary" onClick={onOpenCreate}>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <line x1="12" y1="5" x2="12" y2="19"></line>
                <line x1="5" y1="12" x2="19" y2="12"></line>
              </svg>
              Register New Asset
            </button>
          )}
        </div>
      </div>

      {/* Assets Table */}
      <div className="table-responsive">
        <table className="asset-table">
          <thead>
            <tr>
              <th>Asset Tag & Type</th>
              <th>Brand & Model</th>
              <th>Serial Number</th>
              <th>Status</th>
              <th>Location</th>
              <th>Warranty Status</th>
              <th>Current Assignee</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredAssets.length === 0 ? (
              <tr>
                <td colSpan="8" className="empty-row">
                  <div className="empty-state">
                    <div className="empty-icon">📦</div>
                    <div className="empty-title">No assets found</div>
                    <div className="empty-desc">Try clearing your search query or filters.</div>
                  </div>
                </td>
              </tr>
            ) : (
              filteredAssets.map(asset => {
                const isRetired = asset.status === 'Retired';
                const isInRepair = asset.status === 'In Repair';
                const isAssigned = asset.status === 'Assigned';
                const isInStock = asset.status === 'In Stock';

                return (
                  <tr key={asset.id} className={asset.warrantyExpiringSoon ? 'row-warning' : ''}>
                    <td>
                      <div className="asset-tag-cell">
                        <span className="asset-tag">{asset.assetTag}</span>
                        <span className="asset-type-badge">{asset.assetType}</span>
                      </div>
                    </td>
                    <td>
                      <div className="brand-model-cell">
                        <span className="brand-name">{asset.brand}</span>
                        <span className="model-name">{asset.model}</span>
                      </div>
                    </td>
                    <td>
                      <span className="serial-number">{asset.serialNumber || 'N/A'}</span>
                    </td>
                    <td>
                      <span className={`status-pill ${asset.status.toLowerCase().replace(/\s+/g, '-')}`}>
                        {asset.status}
                      </span>
                    </td>
                    <td>
                      <span className="location-text">{asset.location || 'Unassigned Depot'}</span>
                    </td>
                    <td>
                      {asset.warrantyEndDate ? (
                        <div className="warranty-cell">
                          {asset.warrantyExpired ? (
                            <span className="warranty-tag expired">
                              ⚠️ Expired ({asset.warrantyEndDate})
                            </span>
                          ) : asset.warrantyExpiringSoon ? (
                            <span className="warranty-tag expiring">
                              ⏳ Expiring in {asset.daysUntilWarrantyExpiry}d ({asset.warrantyEndDate})
                            </span>
                          ) : (
                            <span className="warranty-tag valid">
                              ✓ Valid ({asset.warrantyEndDate})
                            </span>
                          )}
                        </div>
                      ) : (
                        <span className="text-muted">No date</span>
                      )}
                    </td>
                    <td>
                      {asset.currentAssignment ? (
                        <div className="assignee-cell">
                          <span className="assignee-name">{asset.currentAssignment.userName}</span>
                          <span className="assignee-dept">{asset.currentAssignment.userDepartment || 'Staff'}</span>
                        </div>
                      ) : (
                        <span className="text-muted">—</span>
                      )}
                    </td>
                    <td>
                      <div className="actions-cell">
                        <button
                          className="btn-action details"
                          onClick={() => onOpenDetails(asset)}
                          title="View Details, History & Tickets"
                        >
                          Details & History
                        </button>

                        {canManageAssets && isInStock && (
                          <button
                            className="btn-action assign"
                            onClick={() => onOpenAssign(asset)}
                            title="Assign to Employee"
                          >
                            Assign
                          </button>
                        )}

                        {canManageAssets && isAssigned && (
                          <button
                            className="btn-action return"
                            onClick={() => onOpenReturn(asset)}
                            title="Process Return"
                          >
                            Return
                          </button>
                        )}

                        {canManageAssets && (isRetired || isInRepair) && (
                          <span className="action-blocked-tip" title="Cannot assign retired or in-repair asset">
                            {isRetired ? 'Locked (Retired)' : 'Locked (In Repair)'}
                          </span>
                        )}
                      </div>
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

