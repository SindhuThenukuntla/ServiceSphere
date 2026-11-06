
import { apiRequest } from "./api";

const slaService = {
  // Get all SLA policies
  getAllPolicies() {
    return apiRequest("/sla/policies");
  },

  // Create a new SLA policy
  createPolicy(policyData) {
    return apiRequest("/sla/policies", {
      method: "POST",
      body: JSON.stringify(policyData),
    });
  },

  // Update an existing SLA policy
  updatePolicy(id, policyData) {
    return apiRequest(`/sla/policies/${id}`, {
      method: "PUT",
      body: JSON.stringify(policyData),
    });
  },

  // Get all tickets
  getAllTickets() {
    return apiRequest("/tickets");
  },

  // Get a ticket's SLA status
  getTicketSlaStatus(ticketId, type) {
    return apiRequest(
      `/sla/tickets/${ticketId}/status?type=${type}`
    );
  },
};

export default slaService;
