import { useEffect, useState } from "react";
import escalationService from "../services/escalationService";
import "../styles/EscalationManagement.css";
function EscalationManagement() {
  const [escalations, setEscalations] = useState([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const [formData, setFormData] = useState({
    ticketId: "",
    escalatedBy: "",
    newAssigneeId: "",
    escalationLevel: "1",
    reason: "",
  });

  const loadEscalations = async () => {
    try {
      setLoading(true);
      const data = await escalationService.getAllEscalations();
      setEscalations(Array.isArray(data) ? data : []);
    } catch (error) {
      setMessage(error.message || "Unable to load escalations");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
  let ignore = false;

  const fetchEscalations = async () => {
    try {
      const data = await escalationService.getAllEscalations();

      if (!ignore) {
        setEscalations(Array.isArray(data) ? data : []);
      }
    } catch (error) {
      if (!ignore) {
        setMessage(error.message || "Unable to load escalations");
      }
    } finally {
      if (!ignore) {
        setLoading(false);
      }
    }
  };

  fetchEscalations();

  return () => {
    ignore = true;
  };
}, []);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");

    const request = {
      ticketId: Number(formData.ticketId),
      escalatedBy: Number(formData.escalatedBy),
      newAssigneeId: Number(formData.newAssigneeId),
      escalationLevel: Number(formData.escalationLevel),
      reason: formData.reason.trim(),
    };

    try {
      await escalationService.createEscalation(request);
      setMessage("Escalation created successfully!");

      setFormData({
        ticketId: "",
        escalatedBy: "",
        newAssigneeId: "",
        escalationLevel: "1",
        reason: "",
      });

      await loadEscalations();
    } catch (error) {
      setMessage(error.message || "Failed to create escalation");
    }
  };

  const handleResolve = async (id) => {
    try {
      await escalationService.resolveEscalation(id);
      setMessage("Escalation resolved successfully!");
      await loadEscalations();
    } catch (error) {
      setMessage(error.message || "Failed to resolve escalation");
    }
  };

  return (
    <div className="escalation-page">
      <h2>Escalation Management</h2>
      <p>Manage ticket escalations and reassignment.</p>

      {message && <p>{message}</p>}

      <form onSubmit={handleSubmit} className="escalation-form">
        <h3>Create Escalation</h3>

        <input
          type="number"
          name="ticketId"
          placeholder="Ticket ID"
          value={formData.ticketId}
          onChange={handleChange}
          min="1"
          required
        />

        <input
          type="number"
          name="escalatedBy"
          placeholder="Escalated By (User ID)"
          value={formData.escalatedBy}
          onChange={handleChange}
          min="1"
          required
        />

        <input
          type="number"
          name="newAssigneeId"
          placeholder="New Assignee (Agent ID)"
          value={formData.newAssigneeId}
          onChange={handleChange}
          min="1"
          required
        />

        <input
          type="number"
          name="escalationLevel"
          placeholder="Escalation Level"
          value={formData.escalationLevel}
          onChange={handleChange}
          min="1"
          required
        />

        <textarea
          name="reason"
          placeholder="Enter escalation reason"
          value={formData.reason}
          onChange={handleChange}
          maxLength={1000}
          required
        />

        <button type="submit">Create Escalation</button>
      </form>

      <section className="escalation-history">
        <h3>Escalation History</h3>

        {loading ? (
          <p>Loading escalations...</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Ticket ID</th>
                <th>Level</th>
                <th>New Assignee</th>
                <th>Reason</th>
                <th>Status</th>
                <th>Action</th>
              </tr>
            </thead>

            <tbody>
              {escalations.map((item) => (
                <tr key={item.id}>
                  <td>{item.id}</td>
                  <td>{item.ticketId}</td>
                  <td>{item.escalationLevel}</td>
                  <td>{item.newAssigneeId}</td>
                  <td>{item.reason}</td>
                  <td>{item.status}</td>
                  <td>
                    {item.status !== "RESOLVED" && (
                      <button onClick={() => handleResolve(item.id)}>
                        Resolve
                      </button>
                    )}
                  </td>
                </tr>
              ))}

              {escalations.length === 0 && (
                <tr>
                  <td colSpan="7">No escalations found.</td>
                </tr>
              )}
            </tbody>
          </table>
        )}
      </section>
    </div>
  );
}

export default EscalationManagement;
