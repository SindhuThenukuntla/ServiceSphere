
package com.servicesphere.escalation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EscalationRequest {

    @NotNull(message = "Ticket ID is required")
    private Long ticketId;

    @NotNull(message = "Escalated by user ID is required")
    private Long escalatedBy;

    @NotNull(message = "New assignee ID is required")
    private Long newAssigneeId;

    @NotNull(message = "Escalation level is required")
    @Min(value = 1, message = "Escalation level must be at least 1")
    private Integer escalationLevel;

    @NotBlank(message = "Escalation reason is required")
    @Size(max = 1000, message = "Reason cannot exceed 1000 characters")
    private String reason;

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getEscalatedBy() {
        return escalatedBy;
    }

    public void setEscalatedBy(Long escalatedBy) {
        this.escalatedBy = escalatedBy;
    }

    public Long getNewAssigneeId() {
        return newAssigneeId;
    }

    public void setNewAssigneeId(Long newAssigneeId) {
        this.newAssigneeId = newAssigneeId;
    }

    public Integer getEscalationLevel() {
        return escalationLevel;
    }

    public void setEscalationLevel(Integer escalationLevel) {
        this.escalationLevel = escalationLevel;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
