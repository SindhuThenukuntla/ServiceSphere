import { useState } from "react";
import queueService from "../services/queueService";
import "../styles/SupportQueues.css";

function SupportQueues() {
  const [agentId, setAgentId] = useState("5");
  const [selectedQueue, setSelectedQueue] = useState("MY_TICKETS");
  const [statusFilter, setStatusFilter] = useState("ALL");

  const [queues, setQueues] = useState({
    unassigned: [],
    myTickets: [],
    highPriority: [],
    overdue: [],
    recentlyUpdated: [],
  });

  const [workload, setWorkload] = useState({
    total: 0,
    open: 0,
  });

  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const loadQueues = async () => {
    if (!agentId || Number(agentId) <= 0) {
      setMessage("Please enter a valid agent ID.");
      return;
    }

    try {
      setLoading(true);
      setMessage("");

      const results = await Promise.allSettled([
        queueService.getUnassignedTickets(),
        queueService.getMyTickets(agentId),
        queueService.getHighPriorityTickets(),
        queueService.getOverdueTickets(),
        queueService.getRecentlyUpdatedTickets(),
        queueService.getAgentWorkload(agentId),
        queueService.getAgentOpenWorkload(agentId),
      ]);

      const getData = (result, fallback) =>
        result.status === "fulfilled" ? result.value : fallback;

      setQueues({
        unassigned: getData(results[0], []),
        myTickets: getData(results[1], []),
        highPriority: getData(results[2], []),
        overdue: getData(results[3], []),
        recentlyUpdated: getData(results[4], []),
      });

      setWorkload({
        total: Number(getData(results[5], 0)),
        open: Number(getData(results[6], 0)),
      });

      const failedCount = results.filter(
        (result) => result.status === "rejected"
      ).length;

      if (failedCount > 0) {
        setMessage(`${failedCount} queue request(s) failed. Check your backend.`);
      }
    } catch (error) {
      setMessage(error.message || "Unable to load support queues.");
    } finally {
      setLoading(false);
    }
  };

  const handleStatusFilter = async (status) => {
    setStatusFilter(status);

    if (status === "ALL") {
      loadQueues();
      return;
    }

    try {
      setLoading(true);
      setMessage("");

      const data = await queueService.getAgentTicketsByStatus(
        agentId,
        status
      );

      setQueues((previous) => ({
        ...previous,
        myTickets: Array.isArray(data) ? data : [],
      }));

      setSelectedQueue("MY_TICKETS");
    } catch (error) {
      setMessage(error.message || "Unable to filter tickets.");
    } finally {
      setLoading(false);
    }
  };

  const getCurrentTickets = () => {
    switch (selectedQueue) {
      case "UNASSIGNED":
        return queues.unassigned;
      case "MY_TICKETS":
        return queues.myTickets;
      case "HIGH_PRIORITY":
        return queues.highPriority;
      case "OVERDUE":
        return queues.overdue;
      case "RECENT":
        return queues.recentlyUpdated;
      default:
        return [];
    }
  };

  const formatDate = (date) => {
    if (!date) return "N/A";

    return new Date(date).toLocaleString();
  };

  const getPriorityClass = (priority) => {
    return String(priority || "").toLowerCase();
  };

  const getStatusClass = (status) => {
    return String(status || "").toLowerCase().replaceAll("_", "-");
  };

  const currentTickets = getCurrentTickets();

  return (
    <div className="support-queues-page">
      <div className="queue-heading">
        <div>
          <h2>Support Queues</h2>
          <p>Monitor ticket assignments, priorities and agent workload.</p>
        </div>

        <div className="agent-selector">
          <label htmlFor="agentId">Agent ID</label>
          <input
            id="agentId"
            type="number"
            min="1"
            value={agentId}
            onChange={(e) => setAgentId(e.target.value)}
          />

          <button onClick={loadQueues} disabled={loading}>
            {loading ? "Loading..." : "Load Queues"}
          </button>
        </div>
      </div>

      {message && <div className="queue-message">{message}</div>}

      <div className="workload-cards">
        <div className="workload-card">
          <span>Total Assigned Tickets</span>
          <strong>{workload.total}</strong>
        </div>

        <div className="workload-card">
          <span>Open Workload</span>
          <strong>{workload.open}</strong>
        </div>

        <div className="workload-card">
          <span>Unassigned Tickets</span>
          <strong>{queues.unassigned.length}</strong>
        </div>

        <div className="workload-card">
          <span>Overdue Tickets</span>
          <strong>{queues.overdue.length}</strong>
        </div>
      </div>

      <section className="queue-section">
        <h3>Ticket Queues</h3>

        <div className="queue-tabs">
          <button
            className={selectedQueue === "MY_TICKETS" ? "active" : ""}
            onClick={() => setSelectedQueue("MY_TICKETS")}
          >
            My Tickets ({queues.myTickets.length})
          </button>

          <button
            className={selectedQueue === "UNASSIGNED" ? "active" : ""}
            onClick={() => setSelectedQueue("UNASSIGNED")}
          >
            Unassigned ({queues.unassigned.length})
          </button>

          <button
            className={selectedQueue === "HIGH_PRIORITY" ? "active" : ""}
            onClick={() => setSelectedQueue("HIGH_PRIORITY")}
          >
            High Priority ({queues.highPriority.length})
          </button>

          <button
            className={selectedQueue === "OVERDUE" ? "active" : ""}
            onClick={() => setSelectedQueue("OVERDUE")}
          >
            Overdue ({queues.overdue.length})
          </button>

          <button
            className={selectedQueue === "RECENT" ? "active" : ""}
            onClick={() => setSelectedQueue("RECENT")}
          >
            Recently Updated ({queues.recentlyUpdated.length})
          </button>
        </div>

        <div className="queue-table-header">
          <h3>
            {selectedQueue === "MY_TICKETS"
              ? "My Assigned Tickets"
              : selectedQueue === "UNASSIGNED"
              ? "Unassigned Tickets"
              : selectedQueue === "HIGH_PRIORITY"
              ? "High Priority Tickets"
              : selectedQueue === "OVERDUE"
              ? "Overdue Tickets"
              : "Recently Updated Tickets"}
          </h3>

          <select
            value={statusFilter}
            onChange={(e) => handleStatusFilter(e.target.value)}
          >
            <option value="ALL">All Statuses</option>
            <option value="OPEN">Open</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="RESOLVED">Resolved</option>
            <option value="CLOSED">Closed</option>
          </select>
        </div>

        {loading ? (
          <p className="queue-empty">Loading tickets...</p>
        ) : currentTickets.length === 0 ? (
          <p className="queue-empty">No tickets found in this queue.</p>
        ) : (
          <div className="queue-table-wrapper">
            <table className="queue-table">
              <thead>
                <tr>
                  <th>Ticket Number</th>
                  <th>Title</th>
                  <th>Priority</th>
                  <th>Status</th>
                  <th>Requester</th>
                  <th>Assignee</th>
                  <th>Resolution Deadline</th>
                  <th>Updated At</th>
                </tr>
              </thead>

              <tbody>
                {currentTickets.map((ticket) => (
                  <tr key={ticket.id}>
                    <td>{ticket.ticketNumber}</td>
                    <td>{ticket.title}</td>

                    <td>
                      <span
                        className={`priority-badge ${getPriorityClass(
                          ticket.priority
                        )}`}
                      >
                        {ticket.priority}
                      </span>
                    </td>

                    <td>
                      <span
                        className={`status-badge ${getStatusClass(
                          ticket.status
                        )}`}
                      >
                        {ticket.status}
                      </span>
                    </td>

                    <td>{ticket.requesterId}</td>
                    <td>{ticket.assigneeId ?? "Unassigned"}</td>
                    <td>{formatDate(ticket.resolutionDueAt)}</td>
                    <td>{formatDate(ticket.updatedAt)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </div>
  );
}

export default SupportQueues;
