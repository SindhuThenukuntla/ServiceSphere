package com.servicesphere.ticket.dto;

import java.time.LocalDateTime;

public class TicketResponse {

    private Long id;
    private String ticketNumber;
    private String title;
    private String description;
    private Long categoryId;
    private String priority;
    private String status;
    private Long requesterId;
    private Long assigneeId;
    private Long departmentId;
    private Long assetId;
    private LocalDateTime responseDueAt;
    private LocalDateTime resolutionDueAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

    public TicketResponse() {
    }

    // Generate getters and setters using IntelliJ:
    // Alt + Insert → Getter and Setter → Select All
}
