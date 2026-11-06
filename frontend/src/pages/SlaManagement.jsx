import { useEffect, useState } from "react";
import slaService from "../services/slaService";
import TicketSlaMonitoring from "./TicketSlaMonitoring";
import "../styles/SlaManagement.css";

function SlaManagement() {
  const [policies, setPolicies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);

  const initialForm = {
    priority: "HIGH",
    responseTargetMinutes: "",
    resolutionTargetMinutes: "",
    warningThresholdPercent: 50,
    enabled: true,
  };

  const [formData, setFormData] = useState(initialForm);
  const [saving, setSaving] = useState(false);

  // Load policies from Spring Boot
  const loadPolicies = async () => {
    try {
      setLoading(true);
      setError("");

      const data = await slaService.getAllPolicies();
      setPolicies(data);
    } catch (err) {
      setError(err.message || "Unable to load SLA policies.");
    } finally {
      setLoading(false);
    }
  };

    useEffect(() => {
    let cancelled = false;

    const fetchPolicies = async () => {
        try {
        setError("");

        const data = await slaService.getAllPolicies();

        if (!cancelled) {
            setPolicies(data);
        }
        } catch (err) {
        if (!cancelled) {
            setError(err.message || "Unable to load SLA policies.");
        }
        } finally {
        if (!cancelled) {
            setLoading(false);
        }
        }
    };

    fetchPolicies();

    return () => {
        cancelled = true;
    };
    }, []);

  // Handle form input
  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]:
        type === "checkbox"
          ? checked
          : type === "number"
            ? Number(value)
            : value,
    }));
  };

  // Open add form
  const handleAdd = () => {
    setEditingId(null);
    setFormData(initialForm);
    setShowForm(true);
  };

  // Open edit form
  const handleEdit = (policy) => {
    setEditingId(policy.id);
    setFormData({
      priority: policy.priority,
      responseTargetMinutes: policy.responseTargetMinutes,
      resolutionTargetMinutes: policy.resolutionTargetMinutes,
      warningThresholdPercent: policy.warningThresholdPercent,
      enabled: policy.enabled,
    });
    setShowForm(true);
  };

  // Save policy
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (
      !formData.responseTargetMinutes ||
      !formData.resolutionTargetMinutes ||
      formData.responseTargetMinutes <= 0 ||
      formData.resolutionTargetMinutes <= 0 ||
      formData.warningThresholdPercent < 1 ||
      formData.warningThresholdPercent > 100
    ) {
      setError("Enter valid SLA targets and a warning threshold between 1 and 100.");
      return;
    }

    try {
      setSaving(true);
      setError("");

      if (editingId) {
        await slaService.updatePolicy(editingId, formData);
      } else {
        await slaService.createPolicy(formData);
      }

      setShowForm(false);
      setFormData(initialForm);
      setEditingId(null);

      await loadPolicies();
    } catch (err) {
      setError(err.message || "Unable to save SLA policy.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="sla-page">
      <div className="page-heading">
        <div>
          <h2>SLA Policy Management</h2>
          <p>Configure response and resolution targets by ticket priority.</p>
        </div>

        <button className="primary-btn" onClick={handleAdd}>
          + Add SLA Policy
        </button>
      </div>

      {error && (
        <div className="error-message" role="alert">
          {error}
          <button onClick={() => setError("")}>Dismiss</button>
        </div>
      )}

      {showForm && (
        <div className="sla-form-card">
          <h3>{editingId ? "Edit SLA Policy" : "Create SLA Policy"}</h3>

          <form onSubmit={handleSubmit}>
            <div className="form-grid">
              <label>
                Priority
                <select
                  name="priority"
                  value={formData.priority}
                  onChange={handleChange}
                  required
                  disabled={Boolean(editingId)}
                >
                  <option value="LOW">Low</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="HIGH">High</option>
                  <option value="CRITICAL">Critical</option>
                </select>
              </label>

              <label>
                Response Target (Minutes)
                <input
                  type="number"
                  name="responseTargetMinutes"
                  min="1"
                  value={formData.responseTargetMinutes}
                  onChange={handleChange}
                  required
                />
              </label>

              <label>
                Resolution Target (Minutes)
                <input
                  type="number"
                  name="resolutionTargetMinutes"
                  min="1"
                  value={formData.resolutionTargetMinutes}
                  onChange={handleChange}
                  required
                />
              </label>

              <label>
                Warning Threshold (%)
                <input
                  type="number"
                  name="warningThresholdPercent"
                  min="1"
                  max="100"
                  value={formData.warningThresholdPercent}
                  onChange={handleChange}
                  required
                />
              </label>
            </div>

            <label className="enabled-field">
              <input
                type="checkbox"
                name="enabled"
                checked={formData.enabled}
                onChange={handleChange}
              />
              Enable this policy
            </label>

            <div className="form-actions">
              <button
                type="button"
                className="secondary-btn"
                onClick={() => setShowForm(false)}
              >
                Cancel
              </button>

              <button
                type="submit"
                className="primary-btn"
                disabled={saving}
              >
                {saving ? "Saving..." : editingId ? "Update Policy" : "Save Policy"}
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="sla-table-card">
        <div className="table-heading">
          <div>
            <h3>SLA Policies</h3>
            <p>Policies retrieved from the ServiceSphere backend.</p>
          </div>

          <button className="secondary-btn" onClick={loadPolicies}>
            ↻ Refresh
          </button>
        </div>

        {loading ? (
          <p className="state-message">Loading SLA policies...</p>
        ) : policies.length === 0 ? (
          <p className="state-message">No SLA policies found.</p>
        ) : (
          <div className="table-wrapper">
            <table className="sla-table">
              <thead>
                <tr>
                  <th>Priority</th>
                  <th>Response Target</th>
                  <th>Resolution Target</th>
                  <th>Warning Threshold</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>

              <tbody>
                {policies.map((policy) => (
                  <tr key={policy.id}>
                    <td>
                      <span className={`priority-badge ${policy.priority.toLowerCase()}`}>
                        {policy.priority}
                      </span>
                    </td>
                    <td>{policy.responseTargetMinutes} min</td>
                    <td>{policy.resolutionTargetMinutes} min</td>
                    <td>{policy.warningThresholdPercent}%</td>
                    <td>
                      <span className={policy.enabled ? "status-enabled" : "status-disabled"}>
                        {policy.enabled ? "Enabled" : "Disabled"}
                      </span>
                    </td>
                    <td>
                      <button
                        className="edit-btn"
                        onClick={() => handleEdit(policy)}
                      >
                        Edit
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
      <TicketSlaMonitoring/>
    </div>
  );
}

export default SlaManagement;
