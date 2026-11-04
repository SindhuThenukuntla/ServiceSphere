import React, { useState } from 'react';
import { createAsset } from '../services/api';

export default function CreateAssetModal({ onClose, onSuccess }) {
  const [formData, setFormData] = useState({
    assetTag: '',
    assetType: 'Laptop',
    brand: '',
    model: '',
    serialNumber: '',
    status: 'In Stock',
    purchaseDate: new Date().toISOString().split('T')[0],
    warrantyEndDate: new Date(Date.now() + 3 * 365 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
    location: 'HQ Storage Room B',
    notes: ''
  });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const assetTypes = ['Laptop', 'Desktop', 'Monitor', 'Phone', 'Peripheral', 'Network Equipment'];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.assetTag.trim()) {
      setError('Asset tag is required');
      return;
    }
    if (!formData.brand.trim() || !formData.model.trim()) {
      setError('Brand and Model are required');
      return;
    }

    try {
      setSubmitting(true);
      setError('');
      await createAsset({
        ...formData,
        assetTag: formData.assetTag.trim().toUpperCase()
      });
      onSuccess(`Asset ${formData.assetTag.toUpperCase()} successfully registered in inventory.`);
      onClose();
    } catch (err) {
      setError(err.message || 'Failed to create asset');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-dialog modal-lg" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Register New IT Asset</h2>
          <button className="btn-close" onClick={onClose}>✕</button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            {error && (
              <div className="form-error-banner">
                <span>⚠️</span> {error}
              </div>
            )}

            <div className="form-grid-2">
              <div className="form-group">
                <label htmlFor="assetTag">Asset Tag (Unique Identifier) *</label>
                <input
                  type="text"
                  id="assetTag"
                  name="assetTag"
                  className="form-control"
                  placeholder="e.g. AST-009"
                  value={formData.assetTag}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="assetType">Asset Type *</label>
                <select
                  id="assetType"
                  name="assetType"
                  className="form-control"
                  value={formData.assetType}
                  onChange={handleChange}
                  required
                >
                  {assetTypes.map(t => (
                    <option key={t} value={t}>{t}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="brand">Brand / Manufacturer *</label>
                <input
                  type="text"
                  id="brand"
                  name="brand"
                  className="form-control"
                  placeholder="e.g. Apple, Dell, Lenovo, Cisco"
                  value={formData.brand}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="model">Model Specification *</label>
                <input
                  type="text"
                  id="model"
                  name="model"
                  className="form-control"
                  placeholder="e.g. MacBook Pro 14 M3, ThinkPad P16"
                  value={formData.model}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="serialNumber">Serial Number (Optional)</label>
                <input
                  type="text"
                  id="serialNumber"
                  name="serialNumber"
                  className="form-control"
                  placeholder="e.g. SN-892301824"
                  value={formData.serialNumber}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label htmlFor="location">Depot Location / Rack</label>
                <input
                  type="text"
                  id="location"
                  name="location"
                  className="form-control"
                  placeholder="e.g. HQ Storage Room B, Server Room 1"
                  value={formData.location}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label htmlFor="purchaseDate">Purchase Date</label>
                <input
                  type="date"
                  id="purchaseDate"
                  name="purchaseDate"
                  className="form-control"
                  value={formData.purchaseDate}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label htmlFor="warrantyEndDate">Warranty Expiration Date</label>
                <input
                  type="date"
                  id="warrantyEndDate"
                  name="warrantyEndDate"
                  className="form-control"
                  value={formData.warrantyEndDate}
                  onChange={handleChange}
                />
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="notes">Technical Specifications & Notes</label>
              <textarea
                id="notes"
                name="notes"
                rows="2"
                className="form-control"
                placeholder="Hardware specs, CPU, RAM, storage, or accessories included..."
                value={formData.notes}
                onChange={handleChange}
              ></textarea>
            </div>
          </div>

          <div className="modal-footer">
            <button type="button" className="btn-secondary" onClick={onClose} disabled={submitting}>
              Cancel
            </button>
            <button type="submit" className="btn-primary" disabled={submitting}>
              {submitting ? 'Registering...' : 'Register Asset'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

