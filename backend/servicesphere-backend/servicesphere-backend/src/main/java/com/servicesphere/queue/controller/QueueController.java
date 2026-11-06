package com.servicesphere.queue.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.servicesphere.queue.service.QueueService;
import com.servicesphere.ticket.entity.Ticket;

@RestController
@RequestMapping("/api/queues")
public class QueueController {

    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    // 1. Get unassigned tickets
    @GetMapping("/unassigned")
    public List<Ticket> getUnassignedTickets() {
        return queueService.getUnassignedTickets();
    }

    // 2. Get tickets assigned to an agent
    @GetMapping("/my-tickets/{agentId}")
    public List<Ticket> getMyTickets(
            @PathVariable Long agentId) {

        return queueService.getMyTickets(agentId);
    }

    // 3. Get high-priority tickets
    @GetMapping("/high-priority")
    public List<Ticket> getHighPriorityTickets() {
        return queueService.getHighPriorityTickets();
    }

    // 4. Get overdue tickets
    @GetMapping("/overdue")
    public List<Ticket> getOverdueTickets() {
        return queueService.getOverdueTickets();
    }

    // 5. Get recently updated tickets
    @GetMapping("/recently-updated")
    public List<Ticket> getRecentlyUpdatedTickets() {
        return queueService.getRecentlyUpdatedTickets();
    }

    // 6. Get agent tickets by status
    @GetMapping("/agent/{agentId}/status/{status}")
    public List<Ticket> getAgentTicketsByStatus(
            @PathVariable Long agentId,
            @PathVariable String status) {

        return queueService.getAgentTicketsByStatus(
                agentId, status);
    }

    // Get total agent workload
    @GetMapping("/agent/{agentId}/workload")
    public long getAgentWorkload(
            @PathVariable Long agentId) {

        return queueService.getAgentWorkload(agentId);
    }

    // Get open ticket workload
    @GetMapping("/agent/{agentId}/workload/open")
    public long getAgentOpenWorkload(
            @PathVariable Long agentId) {

        return queueService.getAgentOpenWorkload(agentId);
    }
}
