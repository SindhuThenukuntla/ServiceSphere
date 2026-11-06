import { apiRequest } from "./api";

const escalationService = {
  // Get all escalations
  getAllEscalations() {
    return apiRequest("/escalations");
  },

  // Create escalation
  createEscalation(escalationData) {
    return apiRequest("/escalations", {
      method: "POST",
      body: JSON.stringify(escalationData),
    });
  },

  // Get escalation history by ticket ID
  getEscalationHistory(ticketId) {
    return apiRequest(`/escalations/ticket/${ticketId}`);
  },

  // Get escalations by status
  getEscalationsByStatus(status) {
    return apiRequest(`/escalations/status/${status}`);
  },

  // Get escalations by agent
  getEscalationsByAgent(agentId) {
    return apiRequest(`/escalations/agent/${agentId}`);
  },

  // Resolve escalation
  resolveEscalation(escalationId) {
    return apiRequest(`/escalations/${escalationId}/resolve`, {
      method: "PUT",
    });
  },
};

export default escalationService;
