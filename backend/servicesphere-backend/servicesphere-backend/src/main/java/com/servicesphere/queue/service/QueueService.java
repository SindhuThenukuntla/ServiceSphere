
package com.servicesphere.queue.service;

import com.servicesphere.ticket.entity.Ticket;
import com.servicesphere.ticket.repository.TicketRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

@Service
public class QueueService {

    private final TicketRepository ticketRepository;

    public QueueService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    // 1. Get unassigned tickets
    public List<Ticket> getUnassignedTickets() {
        return ticketRepository.findByAssigneeIdIsNull();
    }

    // 2. Get tickets assigned to an agent
    public List<Ticket> getMyTickets(Long agentId) {
        return ticketRepository.findByAssigneeId(agentId);
    }

    // 3. Get high and critical priority tickets
    public List<Ticket> getHighPriorityTickets() {
        return ticketRepository.findByPriorityIn(
                Arrays.asList("HIGH", "CRITICAL"));
    }

    // 4. Get overdue unresolved tickets
    public List<Ticket> getOverdueTickets() {
        return ticketRepository
                .findByResolutionDueAtBeforeAndResolvedAtIsNull(
                        LocalDateTime.now());
    }

    // 5. Get recently updated tickets
    public List<Ticket> getRecentlyUpdatedTickets() {
        return ticketRepository.findAllByOrderByUpdatedAtDesc();
    }

    // 6. Get agent tickets by status
    public List<Ticket> getAgentTicketsByStatus(
            Long agentId, String status) {

        return ticketRepository.findByAssigneeIdAndStatus(
                agentId, status);
    }

    // Get total workload of an agent
    public long getAgentWorkload(Long agentId) {
        return ticketRepository.countByAssigneeId(agentId);
    }

    // Get open ticket workload of an agent
    public long getAgentOpenWorkload(Long agentId) {
        return ticketRepository.countByAssigneeIdAndStatus(
                agentId, "OPEN");
    }
}
