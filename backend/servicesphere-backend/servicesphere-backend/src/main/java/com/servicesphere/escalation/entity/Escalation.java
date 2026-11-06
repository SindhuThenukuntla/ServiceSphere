
package com.servicesphere.escalation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_escalations")
public class Escalation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ticketId;

    @Column(nullable = false)
    private Long escalatedBy;

    private Long previousAssigneeId;

    private Long newAssigneeId;

    @Column(nullable = false)
    private Integer escalationLevel;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(nullable = false)
    private String status = "ESCALATED";

    @Column(nullable = false)
    private LocalDateTime escalatedAt;

    private LocalDateTime resolvedAt;

    @PrePersist
    protected void onCreate() {
        escalatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getPreviousAssigneeId() {
        return previousAssigneeId;
    }

    public void setPreviousAssigneeId(Long previousAssigneeId) {
        this.previousAssigneeId = previousAssigneeId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getEscalatedAt() {
        return escalatedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
