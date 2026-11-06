
package com.servicesphere.ticket.service;

import com.servicesphere.ticket.entity.Ticket;
import com.servicesphere.ticket.repository.TicketRepository;

import com.servicesphere.sla.entity.SlaPolicy;
import com.servicesphere.sla.service.SlaService;

import com.servicesphere.notification.service.NotificationService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final SlaService slaService;
    private final NotificationService notificationService;

    public TicketService(
            TicketRepository ticketRepository,
            SlaService slaService,
            NotificationService notificationService) {

        this.ticketRepository = ticketRepository;
        this.slaService = slaService;
        this.notificationService = notificationService;
    }

    // Get all tickets
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    // Get ticket by ID
    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Ticket not found with ID: " + id));
    }

    // Create ticket with automatic SLA and notification
    @Transactional
    public Ticket createTicket(Ticket ticket) {

        LocalDateTime createdTime = LocalDateTime.now();

        // Get SLA policy based on priority
        SlaPolicy policy = slaService.getPolicyByPriority(
                ticket.getPriority());

        // Calculate response deadline
        ticket.setResponseDueAt(
                slaService.calculateDeadline(
                        createdTime,
                        policy.getResponseTargetMinutes()));

        // Calculate resolution deadline
        ticket.setResolutionDueAt(
                slaService.calculateDeadline(
                        createdTime,
                        policy.getResolutionTargetMinutes()));

        // Save ticket first
        Ticket savedTicket = ticketRepository.save(ticket);

        // Automatically notify assigned agent
        if (savedTicket.getAssigneeId() != null) {

            notificationService.createNotification(
                    savedTicket.getAssigneeId(),
                    savedTicket.getId(),
                    "ASSIGNMENT",
                    "New ticket assigned",
                    "Ticket " + savedTicket.getTicketNumber()
                            + " has been assigned to you."
            );
        }

        return savedTicket;
    }

    // Update ticket with automatic reassignment notification
    @Transactional
    public Ticket updateTicket(Long id, Ticket updatedTicket) {

        Ticket existingTicket = getTicketById(id);

        // Store previous agent ID
        Long previousAssigneeId = existingTicket.getAssigneeId();

        existingTicket.setTitle(updatedTicket.getTitle());
        existingTicket.setDescription(updatedTicket.getDescription());
        existingTicket.setPriority(updatedTicket.getPriority());
        existingTicket.setStatus(updatedTicket.getStatus());
        existingTicket.setAssigneeId(updatedTicket.getAssigneeId());

        // Save updated ticket
        Ticket savedTicket = ticketRepository.save(existingTicket);

        // Notify only when a new agent is assigned
        Long newAssigneeId = savedTicket.getAssigneeId();

        if (newAssigneeId != null
                && !Objects.equals(previousAssigneeId, newAssigneeId)) {

            notificationService.createNotification(
                    newAssigneeId,
                    savedTicket.getId(),
                    "ASSIGNMENT",
                    "Ticket assigned to you",
                    "Ticket " + savedTicket.getTicketNumber()
                            + " has been assigned to you."
            );
        }

        return savedTicket;
    }

    // Delete ticket
    public void deleteTicket(Long id) {
        Ticket ticket = getTicketById(id);
        ticketRepository.delete(ticket);
    }

    // Get tickets by status
    public List<Ticket> getTicketsByStatus(String status) {
        return ticketRepository.findByStatus(status);
    }

    // Get tickets by priority
    public List<Ticket> getTicketsByPriority(String priority) {
        return ticketRepository.findByPriority(priority);
    }

    // Get tickets assigned to an agent
    public List<Ticket> getTicketsByAssignee(Long assigneeId) {
        return ticketRepository.findByAssigneeId(assigneeId);
    }

    // Get unassigned tickets
    public List<Ticket> getUnassignedTickets() {
        return ticketRepository.findByAssigneeIdIsNull();
    }
}
