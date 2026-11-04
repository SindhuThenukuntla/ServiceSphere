import React, { useState } from 'react';
import { assignAsset } from '../services/api';

export default function AssignAssetModal({ asset, users, currentUser, onClose, onSuccess }) {
  const [selectedUserId, setSelectedUserId] = useState(users.length > 0 ? users[0].id : '');
  const [conditionOut, setConditionOut] = useState('Brand new / excellent condition with standard accessories');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  if (!asset) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedUserId) {
      setError('Please select an employee');
      return;
    }
    if (!conditionOut.trim()) {
      setError('Please specify condition out');
      return;
    }

    try {
      setSubmitting(true);
      setError('');
      await assignAsset(asset.id, {
        userId: Number(selectedUserId),
        conditionOut: conditionOut.trim(),
        assignedBy: currentUser.id
      });
      onSuccess(`Successfully assigned ${asset.assetTag} to employee.`);
      onClose();
    } catch (err) {
      setError(err.message || 'Failed to assign asset');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-dialog" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <div className="modal-header-title">
            <h2>Assign Asset — {asset.assetTag}</h2>
            <div className="modal-subtitle">{asset.brand} {asset.model} ({asset.assetType})</div>
          </div>
          <button className="btn-close" onClick={onClose}>✕</button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            {error && (
              <div className="form-error-banner">
                <span>⚠️</span> {error}
              </div>
            )}

            {/* Integrity Warning */}
            <div className="rule-info-box">
              <span className="info-icon">ℹ️</span>
              <div>
                <strong>Integrity & Transaction Policy:</strong> Assets may have multiple historical assignments but no more than one active assignment. Retired or in-repair assets are locked from assignment.
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="assign-user">Select Employee / Requester *</label>
              <select
                id="assign-user"
                className="form-control"
                value={selectedUserId}
                onChange={e => setSelectedUserId(e.target.value)}
                required
              >
                {users.map(u => (
                  <option key={u.id} value={u.id}>
                    {u.fullName} — {u.email} ({u.department || 'Staff'})
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="condition-out">Condition Out (Inspection Notes) *</label>
              <textarea
                id="condition-out"
                className="form-control"
                rows="3"
                value={conditionOut}
                onChange={e => setConditionOut(e.target.value)}
                placeholder="e.g. Brand new in sealed box with 65W charger and protective sleeve"
                required
              ></textarea>
            </div>

            <div className="form-group">
              <label>Assigned By (Staff Actor)</label>
              <input
                type="text"
                className="form-control"
                value={`${currentUser.fullName} (${currentUser.role})`}
                disabled
              />
            </div>
          </div>

          <div className="modal-footer">
            <button type="button" className="btn-secondary" onClick={onClose} disabled={submitting}>
              Cancel
            </button>
            <button type="submit" className="btn-primary" disabled={submitting}>
              {submitting ? 'Assigning...' : 'Confirm Assignment'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

