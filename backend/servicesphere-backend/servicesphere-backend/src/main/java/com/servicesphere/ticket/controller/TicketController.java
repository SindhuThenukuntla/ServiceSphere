
package com.servicesphere.ticket.controller;

import com.servicesphere.ticket.dto.TicketRequest;
import com.servicesphere.ticket.dto.TicketResponse;
import com.servicesphere.ticket.entity.Ticket;
import com.servicesphere.ticket.service.TicketService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // Create ticket
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ticket createTicket(@RequestBody TicketRequest request) {

        Ticket ticket = new Ticket();

        ticket.setTicketNumber(request.getTicketNumber());
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setCategoryId(request.getCategoryId());
        ticket.setPriority(request.getPriority());
        ticket.setStatus(request.getStatus());
        ticket.setRequesterId(request.getRequesterId());
        ticket.setAssigneeId(request.getAssigneeId());
        ticket.setDepartmentId(request.getDepartmentId());
        ticket.setAssetId(request.getAssetId());

        return ticketService.createTicket(ticket);
    }

    // Get all tickets
    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    // Get ticket by ID
    @GetMapping("/{id}")
    public Ticket getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }

    // Update ticket
    @PutMapping("/{id}")
    public Ticket updateTicket(
            @PathVariable Long id,
            @RequestBody TicketRequest request) {

        Ticket updatedTicket = new Ticket();

        updatedTicket.setTitle(request.getTitle());
        updatedTicket.setDescription(request.getDescription());
        updatedTicket.setPriority(request.getPriority());
        updatedTicket.setStatus(request.getStatus());
        updatedTicket.setAssigneeId(request.getAssigneeId());

        return ticketService.updateTicket(id, updatedTicket);
    }

    // Delete ticket
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
    }

    // Filter by status
    @GetMapping("/status/{status}")
    public List<Ticket> getTicketsByStatus(@PathVariable String status) {
        return ticketService.getTicketsByStatus(status);
    }

    // Filter by priority
    @GetMapping("/priority/{priority}")
    public List<Ticket> getTicketsByPriority(@PathVariable String priority) {
        return ticketService.getTicketsByPriority(priority);
    }

    // Unassigned tickets
    @GetMapping("/unassigned")
    public List<Ticket> getUnassignedTickets() {
        return ticketService.getUnassignedTickets();
    }

    // Tickets assigned to an agent
    @GetMapping("/assignee/{assigneeId}")
    public List<Ticket> getTicketsByAssignee(
            @PathVariable Long assigneeId) {
        return ticketService.getTicketsByAssignee(assigneeId);
    }
}
