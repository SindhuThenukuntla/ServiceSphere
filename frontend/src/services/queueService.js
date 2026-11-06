import { apiRequest } from "./api";

const queueService = {
  // Get unassigned tickets
  getUnassignedTickets() {
    return apiRequest("/queues/unassigned");
  },

  // Get tickets assigned to an agent
  getMyTickets(agentId) {
    return apiRequest(`/queues/my-tickets/${agentId}`);
  },

  // Get high-priority tickets
  getHighPriorityTickets() {
    return apiRequest("/queues/high-priority");
  },

  // Get overdue tickets
  getOverdueTickets() {
    return apiRequest("/queues/overdue");
  },

  // Get recently updated tickets
  getRecentlyUpdatedTickets() {
    return apiRequest("/queues/recently-updated");
  },

  // Get agent tickets by status
  getAgentTicketsByStatus(agentId, status) {
    return apiRequest(`/queues/agent/${agentId}/status/${status}`);
  },

  // Get total agent workload
  getAgentWorkload(agentId) {
    return apiRequest(`/queues/agent/${agentId}/workload`);
  },

  // Get open agent workload
  getAgentOpenWorkload(agentId) {
    return apiRequest(`/queues/agent/${agentId}/workload/open`);
  },
};

export default queueService;
