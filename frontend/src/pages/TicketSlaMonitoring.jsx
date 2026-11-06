
import { useCallback, useEffect, useState } from "react";
import slaService from "../services/slaService";
import "../styles/TicketSlaMonitoring.css";

function TicketSlaMonitoring() {
  const [ticketId, setTicketId] = useState("");
  const [slaType, setSlaType] = useState("RESPONSE");
  const [slaData, setSlaData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const [tickets, setTickets] = useState([]);
  const [monitoringLoading, setMonitoringLoading] = useState(true);
  const [monitoringError, setMonitoringError] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");

  // Load all tickets and their SLA statuses
  const loadMonitoring = useCallback(async () => {
    try {
      setMonitoringLoading(true);
      setMonitoringError("");

      const ticketList = await slaService.getAllTickets();

      if (!Array.isArray(ticketList)) {
        throw new Error("Invalid ticket data received from backend.");
      }

      const monitoringData = await Promise.all(
        ticketList.map(async (ticket) => {
          const responsePromise =
            ticket.createdAt && ticket.responseDueAt
              ? slaService.getTicketSlaStatus(ticket.id, "RESPONSE")
              : Promise.resolve(null);

          const resolutionPromise =
            ticket.createdAt && ticket.resolutionDueAt
              ? slaService.getTicketSlaStatus(ticket.id, "RESOLUTION")
              : Promise.resolve(null);

          const [responseResult, resolutionResult] =
            await Promise.allSettled([
              responsePromise,
              resolutionPromise,
            ]);

          return {
            ...ticket,
            responseSla:
              responseResult.status === "fulfilled"
                ? responseResult.value
                : null,
            resolutionSla:
              resolutionResult.status === "fulfilled"
                ? resolutionResult.value
                : null,
          };
        })
      );

      setTickets(monitoringData);
    } catch (err) {
      setMonitoringError(
        err.message || "Unable to load ticket monitoring data."
      );
    } finally {
      setMonitoringLoading(false);
    }
  }, []);

  useEffect(() => {
  const timer = setTimeout(() => {
    loadMonitoring();
  }, 0);

  return () => clearTimeout(timer);
}, [loadMonitoring]);

  // Manually check one ticket
  const handleSearch = async (e) => {
    e.preventDefault();

    if (!ticketId.trim() || Number(ticketId) <= 0) {
      setError("Please enter a valid ticket ID.");
      return;
    }

    try {
      setLoading(true);
      setError("");
      setSlaData(null);

      const data = await slaService.getTicketSlaStatus(
        ticketId,
        slaType
      );

      setSlaData(data);
    } catch (err) {
      setError(err.message || "Unable to fetch ticket SLA status.");
    } finally {
      setLoading(false);
    }
  };

  const getStatusClass = (status) => {
    switch (status?.toUpperCase()) {
      case "ON_TRACK":
        return "on-track";
      case "AT_RISK":
        return "at-risk";
      case "BREACHED":
        return "breached";
      default:
        return "unavailable";
    }
  };

  const getStatusLabel = (sla) => {
    if (!sla) return "Unavailable";
    return sla.status?.replaceAll("_", " ") || "Unavailable";
  };

  // Search and filter tickets
  const filteredTickets = tickets.filter((ticket) => {
    const search = searchTerm.toLowerCase();

    const matchesSearch =
      ticket.ticketNumber?.toLowerCase().includes(search) ||
      ticket.title?.toLowerCase().includes(search) ||
      String(ticket.id).includes(search);

    const responseStatus = ticket.responseSla?.status;
    const resolutionStatus = ticket.resolutionSla?.status;

    const matchesStatus =
      statusFilter === "ALL" ||
      responseStatus === statusFilter ||
      resolutionStatus === statusFilter;

    return matchesSearch && matchesStatus;
  });

  
const getTicketOverallStatus = (ticket) => {
  const statuses = [
    ticket.responseSla?.status,
    ticket.resolutionSla?.status,
  ];

  if (statuses.includes("BREACHED")) return "BREACHED";
  if (statuses.includes("AT_RISK")) return "AT_RISK";
  if (statuses.includes("ON_TRACK")) return "ON_TRACK";

  return "UNAVAILABLE";
};

const onTrackCount = tickets.filter(
  (ticket) => getTicketOverallStatus(ticket) === "ON_TRACK"
).length;

const atRiskCount = tickets.filter(
  (ticket) => getTicketOverallStatus(ticket) === "AT_RISK"
).length;

const breachedCount = tickets.filter(
  (ticket) => getTicketOverallStatus(ticket) === "BREACHED"
).length;

const unavailableCount = tickets.filter(
  (ticket) => getTicketOverallStatus(ticket) === "UNAVAILABLE"
).length;


  return (
    <section className="ticket-sla-section">

      {/* Manual SLA Checker */}
      <div className="ticket-sla-card">
        <div className="ticket-sla-heading">
          <div>
            <h3>Ticket SLA Monitoring</h3>
            <p>Check response and resolution SLA status for a ticket.</p>
          </div>
        </div>

        <form className="ticket-sla-search" onSubmit={handleSearch}>
          <div className="ticket-sla-field">
            <label htmlFor="ticketId">Ticket ID</label>
            <input
              id="ticketId"
              type="number"
              min="1"
              value={ticketId}
              onChange={(e) => setTicketId(e.target.value)}
              placeholder="Enter ticket ID"
              required
            />
          </div>

          <div className="ticket-sla-field">
            <label htmlFor="slaType">SLA Type</label>
            <select
              id="slaType"
              value={slaType}
              onChange={(e) => setSlaType(e.target.value)}
            >
              <option value="RESPONSE">Response SLA</option>
              <option value="RESOLUTION">Resolution SLA</option>
            </select>
          </div>

          <button
            type="submit"
            className="primary-btn"
            disabled={loading}
          >
            {loading ? "Checking..." : "Check SLA"}
          </button>
        </form>

        {error && (
          <div className="error-message" role="alert">
            {error}
          </div>
        )}

        {slaData && (
          <div className="ticket-sla-result">
            <div className="ticket-sla-result-heading">
              <h4>{slaType} SLA Result</h4>

              <span className={`sla-status ${getStatusClass(slaData.status)}`}>
                {getStatusLabel(slaData)}
              </span>
            </div>

            <div className="ticket-sla-result-grid">
              <div>
                <span>Remaining Time</span>
                <strong>{slaData.remainingMinutes} minutes</strong>
              </div>

              <div>
                <span>Elapsed Percentage</span>
                <strong>
                  {Number(slaData.elapsedPercentage).toFixed(2)}%
                </strong>
              </div>
            </div>

            <div className="sla-progress-section">
              <div className="sla-progress-heading">
                <span>SLA Time Progress</span>
                <strong>
                  {Number(slaData.elapsedPercentage).toFixed(2)}%
                </strong>
              </div>

              <div className="sla-progress-track">
                <div
                  className={`sla-progress-fill ${getStatusClass(slaData.status)}`}
                  style={{
                    width: `${Math.min(
                      100,
                      Math.max(0, Number(slaData.elapsedPercentage) || 0)
                    )}%`,
                  }}
                />
              </div>
            </div>

            <p className="sla-result-message">{slaData.message}</p>
          </div>
        )}
      </div>

      {/* Automatic Ticket Monitoring */}
      <div className="ticket-sla-card automatic-monitoring">
        <div className="ticket-sla-heading">
          <div>
            <h3>All Ticket SLA Status</h3>
            <p>Automatically monitored tickets from the backend.</p>
          </div>

          <button
            className="secondary-btn"
            onClick={loadMonitoring}
            disabled={monitoringLoading}
          >
            {monitoringLoading ? "Loading..." : "↻ Refresh"}
          </button>
        </div>

        {/* Summary Cards */}
        <div className="monitoring-summary">
          <div className="monitoring-stat">
            <span>Total Tickets</span>
            <strong>{tickets.length}</strong>
          </div>

          <div className="monitoring-stat">
            <span>On Track</span>
            <strong className="text-green">{onTrackCount}</strong>
          </div>

          <div className="monitoring-stat">
            <span>At Risk</span>
            <strong className="text-orange">{atRiskCount}</strong>
          </div>

          <div className="monitoring-stat">
            <span>Breached</span>
            <strong className="text-red">{breachedCount}</strong>
          </div>
          <div className="monitoring-stat">
                <span>Unavailable</span>
                <strong>{unavailableCount}</strong>
            </div>
        </div>

        {/* Search and Filter */}
        <div className="monitoring-filters">
          <input
            type="text"
            placeholder="Search ticket number or title..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            aria-label="Search tickets"
          />

          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            aria-label="Filter by SLA status"
          >
            <option value="ALL">All SLA statuses</option>
            <option value="ON_TRACK">On Track</option>
            <option value="AT_RISK">At Risk</option>
            <option value="BREACHED">Breached</option>
          </select>
        </div>

        {monitoringError && (
          <div className="error-message" role="alert">
            {monitoringError}
            <button onClick={loadMonitoring}>Retry</button>
          </div>
        )}

        {monitoringLoading ? (
          <p className="state-message">Loading ticket SLA information...</p>
        ) : !monitoringError && filteredTickets.length === 0 ? (
          <p className="state-message">
            No tickets match your search or filter.
          </p>
        ) : !monitoringError ? (
          <div className="table-wrapper">
            <table className="ticket-monitoring-table">
              <thead>
                <tr>
                  <th>Ticket</th>
                  <th>Priority</th>
                  <th>Response SLA</th>
                  <th>Response Remaining</th>
                  <th>Resolution SLA</th>
                  <th>Resolution Remaining</th>
                </tr>
              </thead>

              <tbody>
                {filteredTickets.map((ticket) => (
                  <tr key={ticket.id}>
                    <td>
                      <strong>{ticket.ticketNumber}</strong>
                      <span className="monitoring-ticket-title">
                        {ticket.title}
                      </span>
                    </td>

                    <td>
                      <span
                        className={`priority-badge ${ticket.priority?.toLowerCase()}`}
                      >
                        {ticket.priority}
                      </span>
                    </td>

                    <td>
                      <span
                        className={`sla-status ${getStatusClass(ticket.responseSla?.status)}`}
                      >
                        {getStatusLabel(ticket.responseSla)}
                      </span>
                    </td>

                    <td>
                      {ticket.responseSla
                        ? `${ticket.responseSla.remainingMinutes} min`
                        : "—"}
                    </td>

                    <td>
                      <span
                        className={`sla-status ${getStatusClass(ticket.resolutionSla?.status)}`}
                      >
                        {getStatusLabel(ticket.resolutionSla)}
                      </span>
                    </td>

                    <td>
                      {ticket.resolutionSla
                        ? `${ticket.resolutionSla.remainingMinutes} min`
                        : "—"}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : null}
      </div>
    </section>
  );
}

export default TicketSlaMonitoring;
