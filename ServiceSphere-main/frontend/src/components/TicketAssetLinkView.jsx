import React, { useState, useEffect } from 'react';
import { fetchAllTickets, linkAssetToTicket, unlinkAssetFromTicket } from '../services/api';

export default function TicketAssetLinkView({ assets, users, onRefresh }) {
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedTicketId, setSelectedTicketId] = useState('');
  const [selectedAssetId, setSelectedAssetId] = useState('');
  const [resultMsg, setResultMsg] = useState({ text: '', type: '' });
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    loadTickets();
  }, []);

  const loadTickets = async () => {
    setLoading(true);
    try {
      const data = await fetchAllTickets();
      setTickets(data);
      if (data.length > 0) setSelectedTicketId(data[0].id);
      if (assets.length > 0) setSelectedAssetId(assets[0].id);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleLink = async (e) => {
    e.preventDefault();
    if (!selectedTicketId || !selectedAssetId) {
      setResultMsg({ text: 'Please select both a ticket and an asset', type: 'error' });
      return;
    }

    try {
      setIsSubmitting(true);
      setResultMsg({ text: '', type: '' });
      const updated = await linkAssetToTicket(selectedTicketId, selectedAssetId);
      setResultMsg({
        text: `✓ Successfully linked ${updated.linkedAsset.assetTag} (${updated.linkedAsset.brand} ${updated.linkedAsset.model}) to ${updated.ticketNumber}. Transaction validated!`,
        type: 'success'
      });
      loadTickets();
      onRefresh();
    } catch (err) {
      setResultMsg({
        text: `⚠️ Integrity Rejection: ${err.message}`,
        type: 'error'
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleUnlink = async (ticketId) => {
    try {
      setResultMsg({ text: '', type: '' });
      await unlinkAssetFromTicket(ticketId);
      setResultMsg({ text: 'Asset unlinked successfully from ticket.', type: 'success' });
      loadTickets();
      onRefresh();
    } catch (err) {
      setResultMsg({ text: err.message || 'Failed to unlink', type: 'error' });
    }
  };

  return (
    <div className="module-container">
      <div className="view-intro-banner">
        <div className="intro-icon">🔗</div>
        <div>
          <h2>Cross-Team Contribution: Ticket-Asset Linking & Integrity Validation</h2>
          <p>
            Student 3 coordinates with Ticket Management to validate relational integrity.
            Operational tickets can be linked to hardware assets for maintenance tracking, while transaction rules
            prevent linking retired assets to active service requests.
          </p>
        </div>
      </div>

      {/* Interactive Testing Widget */}
      <div className="card-box test-linking-card">
        <div className="card-box-header">
          <h3>Test Ticket-Asset Linking Rules</h3>
          <span className="badge-rule">Integrity & Transaction Engine</span>
        </div>

        <form onSubmit={handleLink} className="linking-form">
          <div className="form-grid-2">
            <div className="form-group">
              <label htmlFor="select-ticket">Select Ticket *</label>
              <select
                id="select-ticket"
                className="form-control"
                value={selectedTicketId}
                onChange={e => setSelectedTicketId(e.target.value)}
                required
              >
                {tickets.map(t => (
                  <option key={t.id} value={t.id}>
                    {t.ticketNumber}: {t.title} ({t.status} - {t.priority})
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="select-asset">Select Hardware Asset *</label>
              <select
                id="select-asset"
                className="form-control"
                value={selectedAssetId}
                onChange={e => setSelectedAssetId(e.target.value)}
                required
              >
                {assets.map(a => (
                  <option key={a.id} value={a.id}>
                    {a.assetTag} — {a.brand} {a.model} [Status: {a.status}]
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="form-actions-inline">
            <button type="submit" className="btn-primary" disabled={isSubmitting}>
              {isSubmitting ? 'Validating...' : 'Execute Link Transaction'}
            </button>
            <span className="hint-text">
              Try selecting <strong>AST-007 (Retired Mouse)</strong> with an Open ticket to verify atomic rejection rule.
            </span>
          </div>

          {resultMsg.text && (
            <div className={`result-alert-box ${resultMsg.type}`}>
              {resultMsg.text}
            </div>
          )}
        </form>
      </div>

      {/* Tickets & Linked Assets Table */}
      <div className="table-responsive">
        <h3 className="section-title">ITSM Support Tickets & Linked Assets</h3>
        <table className="asset-table">
          <thead>
            <tr>
              <th>Ticket Number</th>
              <th>Title & Description</th>
              <th>Requester</th>
              <th>Priority</th>
              <th>Status</th>
              <th>Linked Asset</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="7" className="text-center py-4">Loading ticket-asset relationships...</td>
              </tr>
            ) : tickets.length === 0 ? (
              <tr>
                <td colSpan="7" className="empty-row">No support tickets found.</td>
              </tr>
            ) : (
              tickets.map(t => (
                <tr key={t.id}>
                  <td>
                    <span className="ticket-code">{t.ticketNumber}</span>
                  </td>
                  <td>
                    <div className="ticket-title-col">
                      <strong>{t.title}</strong>
                      <span className="ticket-desc-snippet">{t.description}</span>
                    </div>
                  </td>
                  <td>
                    <span className="assignee-name">{t.requesterName}</span>
                  </td>
                  <td>
                    <span className={`priority-tag ${(t.priority || 'Medium').toLowerCase()}`}>
                      {t.priority || 'Medium'}
                    </span>
                  </td>
                  <td>
                    <span className="ticket-status-pill">{t.status}</span>
                  </td>
                  <td>
                    {t.linkedAsset ? (
                      <div className="linked-asset-pill">
                        <span className="tag">{t.linkedAsset.assetTag}</span>
                        <span className="name">{t.linkedAsset.brand} {t.linkedAsset.model}</span>
                        <span className={`status-micro ${t.linkedAsset.status.toLowerCase().replace(/\s+/g, '-')}`}>
                          {t.linkedAsset.status}
                        </span>
                      </div>
                    ) : (
                      <span className="text-muted">No asset linked</span>
                    )}
                  </td>
                  <td>
                    {t.linkedAsset ? (
                      <button
                        className="btn-action return"
                        onClick={() => handleUnlink(t.id)}
                        title="Unlink asset from ticket"
                      >
                        Unlink
                      </button>
                    ) : (
                      <button
                        className="btn-action assign"
                        onClick={() => {
                          setSelectedTicketId(t.id);
                          window.scrollTo({ top: 0, behavior: 'smooth' });
                        }}
                      >
                        Link Asset
                      </button>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

