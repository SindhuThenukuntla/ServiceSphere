
package com.servicesphere.escalation.controller;

import com.servicesphere.escalation.dto.EscalationRequest;
import com.servicesphere.escalation.entity.Escalation;
import com.servicesphere.escalation.service.EscalationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/escalations")
public class EscalationController {

    private final EscalationService escalationService;

    public EscalationController(
            EscalationService escalationService) {
        this.escalationService = escalationService;
    }

    // Escalate a ticket
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Escalation escalateTicket(
            @Valid @RequestBody EscalationRequest request) {

        return escalationService.escalateTicket(
                request.getTicketId(),
                request.getEscalatedBy(),
                request.getNewAssigneeId(),
                request.getEscalationLevel(),
                request.getReason()
        );
    }

    // Get all escalations
    @GetMapping
    public List<Escalation> getAllEscalations() {
        return escalationService.getAllEscalations();
    }

    // Get escalation history for a ticket
    @GetMapping("/ticket/{ticketId}")
    public List<Escalation> getEscalationHistory(
            @PathVariable Long ticketId) {

        return escalationService.getEscalationHistory(ticketId);
    }

    // Get escalations by status
    @GetMapping("/status/{status}")
    public List<Escalation> getEscalationsByStatus(
            @PathVariable String status) {

        return escalationService.getEscalationsByStatus(status);
    }

    // Get escalations assigned to an agent
    @GetMapping("/agent/{agentId}")
    public List<Escalation> getEscalationsByAgent(
            @PathVariable Long agentId) {

        return escalationService.getEscalationsByAgent(agentId);
    }

    // Resolve escalation
    @PutMapping("/{escalationId}/resolve")
    public Escalation resolveEscalation(
            @PathVariable Long escalationId) {

        return escalationService.resolveEscalation(escalationId);
    }
}
