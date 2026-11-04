import React, { useState } from 'react';
import { returnAsset } from '../services/api';

export default function ReturnAssetModal({ asset, onClose, onSuccess }) {
  const [conditionIn, setConditionIn] = useState('Returned in good working condition. Cleaned and reset.');
  const [nextStatus, setNextStatus] = useState('In Stock');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  if (!asset) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!conditionIn.trim()) {
      setError('Please provide return condition inspection notes');
      return;
    }

    try {
      setSubmitting(true);
      setError('');
      await returnAsset(asset.id, {
        conditionIn: conditionIn.trim(),
        nextStatus
      });
      onSuccess(`Successfully returned ${asset.assetTag}. Status updated to '${nextStatus}'.`);
      onClose();
    } catch (err) {
      setError(err.message || 'Failed to process asset return');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-dialog" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <div className="modal-header-title">
            <h2>Process Asset Return — {asset.assetTag}</h2>
            <div className="modal-subtitle">{asset.brand} {asset.model}</div>
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

            {asset.currentAssignment && (
              <div className="return-meta-box">
                <div className="return-meta-row">
                  <span className="label">Current Assignee:</span>
                  <span className="val font-semibold">{asset.currentAssignment.userName}</span>
                </div>
                <div className="return-meta-row">
                  <span className="label">Assigned At:</span>
                  <span className="val">{new Date(asset.currentAssignment.assignedAt).toLocaleDateString()}</span>
                </div>
                <div className="return-meta-row">
                  <span className="label">Original Condition Out:</span>
                  <span className="val">{asset.currentAssignment.conditionOut}</span>
                </div>
              </div>
            )}

            <div className="form-group">
              <label htmlFor="condition-in">Condition In (Intake Inspection Notes) *</label>
              <textarea
                id="condition-in"
                className="form-control"
                rows="3"
                value={conditionIn}
                onChange={e => setConditionIn(e.target.value)}
                placeholder="e.g. Returned in good working condition. Screen intact, minor surface scuffs on casing."
                required
              ></textarea>
            </div>

            <div className="form-group">
              <label htmlFor="next-status">Post-Return Status Designation *</label>
              <select
                id="next-status"
                className="form-control"
                value={nextStatus}
                onChange={e => setNextStatus(e.target.value)}
                required
              >
                <option value="In Stock">In Stock (Ready for immediate redeployment)</option>
                <option value="In Repair">In Repair (Requires maintenance / hardware diagnostic)</option>
                <option value="Retired">Retired (Decommission from corporate inventory)</option>
              </select>
              <div className="field-hint">
                Atomic transaction will close the active assignment record and transition the asset status to <strong>{nextStatus}</strong>.
              </div>
            </div>
          </div>

          <div className="modal-footer">
            <button type="button" className="btn-secondary" onClick={onClose} disabled={submitting}>
              Cancel
            </button>
            <button type="submit" className="btn-danger" disabled={submitting}>
              {submitting ? 'Processing Return...' : 'Confirm Return'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

