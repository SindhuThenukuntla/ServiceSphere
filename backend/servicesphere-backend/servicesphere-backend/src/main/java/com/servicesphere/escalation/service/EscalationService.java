
package com.servicesphere.escalation.service;

import com.servicesphere.escalation.entity.Escalation;
import com.servicesphere.escalation.repository.EscalationRepository;

import com.servicesphere.ticket.entity.Ticket;
import com.servicesphere.ticket.repository.TicketRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.servicesphere.notification.service.NotificationService;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EscalationService {

    private final EscalationRepository escalationRepository;
    private final TicketRepository ticketRepository;
    private final NotificationService notificationService;
    public EscalationService(
            EscalationRepository escalationRepository,
            TicketRepository ticketRepository,
            NotificationService notificationService) {

        this.escalationRepository = escalationRepository;
        this.ticketRepository = ticketRepository;
        this.notificationService = notificationService;
    }

    // Escalate ticket and reassign to another agent

    @Transactional
    public Escalation escalateTicket(
            Long ticketId,
            Long escalatedBy,
            Long newAssigneeId,
            Integer escalationLevel,
            String reason) {

        if (escalatedBy == null || newAssigneeId == null) {
            throw new IllegalArgumentException(
                    "Escalator and new assignee are required");
        }

        if (escalationLevel == null || escalationLevel < 1) {
            throw new IllegalArgumentException(
                    "Escalation level must be at least 1");
        }

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException(
                    "Escalation reason is required");
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Ticket not found: " + ticketId));

        Escalation escalation = new Escalation();

        escalation.setTicketId(ticketId);
        escalation.setEscalatedBy(escalatedBy);
        escalation.setPreviousAssigneeId(ticket.getAssigneeId());
        escalation.setNewAssigneeId(newAssigneeId);
        escalation.setEscalationLevel(escalationLevel);
        escalation.setReason(reason);
        escalation.setStatus("ESCALATED");

        // Reassign ticket
        ticket.setAssigneeId(newAssigneeId);
        ticketRepository.save(ticket);

        // Save escalation history
        Escalation savedEscalation =
                escalationRepository.save(escalation);

        // Automatically notify new agent
        notificationService.createNotification(
                newAssigneeId,
                ticket.getId(),
                "ESCALATION",
                "Ticket escalated to you",
                "Ticket " + ticket.getTicketNumber()
                        + " has been escalated to you. Reason: "
                        + reason
        );

        return savedEscalation;
    }


    // Get all escalations
    public List<Escalation> getAllEscalations() {
        return escalationRepository.findAll();
    }

    // Get escalation history for a ticket
    public List<Escalation> getEscalationHistory(Long ticketId) {

        return escalationRepository
                .findByTicketIdOrderByEscalatedAtDesc(ticketId);
    }

    // Get escalations by status
    public List<Escalation> getEscalationsByStatus(String status) {
        return escalationRepository.findByStatus(status);
    }

    // Get escalations assigned to an agent
    public List<Escalation> getEscalationsByAgent(Long agentId) {
        return escalationRepository.findByNewAssigneeId(agentId);
    }

    // Resolve an escalation
    @Transactional
    public Escalation resolveEscalation(Long escalationId) {

        Escalation escalation = escalationRepository.findById(escalationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Escalation not found: " + escalationId));

        if ("RESOLVED".equals(escalation.getStatus())) {
            throw new IllegalStateException(
                    "Escalation is already resolved");
        }

        escalation.setStatus("RESOLVED");
        escalation.setResolvedAt(LocalDateTime.now());

        return escalationRepository.save(escalation);
    }
}
