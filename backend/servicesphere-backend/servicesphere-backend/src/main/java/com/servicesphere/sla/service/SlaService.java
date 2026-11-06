
package com.servicesphere.sla.service;

import com.servicesphere.sla.dto.SlaStatusResponse;
import com.servicesphere.sla.entity.SlaPolicy;
import com.servicesphere.sla.repository.SlaPolicyRepository;

import com.servicesphere.ticket.entity.Ticket;
import com.servicesphere.ticket.repository.TicketRepository;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SlaService {

    private final SlaPolicyRepository slaPolicyRepository;
    private final TicketRepository ticketRepository;

    public SlaService(
            SlaPolicyRepository slaPolicyRepository,
            TicketRepository ticketRepository) {

        this.slaPolicyRepository = slaPolicyRepository;
        this.ticketRepository = ticketRepository;
    }

    // 1. Create SLA policy
    public SlaPolicy createPolicy(SlaPolicy policy) {

        String priority = policy.getPriority().toUpperCase();

        if (slaPolicyRepository.existsByPriorityIgnoreCase(priority)) {
            throw new IllegalArgumentException(
                    "SLA policy already exists for: " + priority);
        }

        policy.setPriority(priority);

        return slaPolicyRepository.save(policy);
    }

    // 2. Get all SLA policies
    public List<SlaPolicy> getAllPolicies() {
        return slaPolicyRepository.findAll();
    }

    // 3. Get enabled SLA policies
    public List<SlaPolicy> getEnabledPolicies() {
        return slaPolicyRepository.findByEnabledTrue();
    }

    // 4. Get policy by priority
    public SlaPolicy getPolicyByPriority(String priority) {

        return slaPolicyRepository
                .findByPriorityIgnoreCase(priority)
                .filter(policy -> Boolean.TRUE.equals(policy.getEnabled()))
                .orElseThrow(() ->
                        new RuntimeException(
                                "Active SLA policy not found: " + priority));
    }

    // 5. Update SLA policy
    public SlaPolicy updatePolicy(
            Long id,
            SlaPolicy updatedPolicy) {

        SlaPolicy existing = slaPolicyRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("SLA policy not found: " + id));

        existing.setResponseTargetMinutes(
                updatedPolicy.getResponseTargetMinutes());

        existing.setResolutionTargetMinutes(
                updatedPolicy.getResolutionTargetMinutes());

        existing.setWarningThresholdPercent(
                updatedPolicy.getWarningThresholdPercent());

        existing.setEnabled(updatedPolicy.getEnabled());

        return slaPolicyRepository.save(existing);
    }

    // 6. Calculate SLA deadline
    public LocalDateTime calculateDeadline(
            LocalDateTime startTime,
            Integer targetMinutes) {

        if (startTime == null || targetMinutes == null
                || targetMinutes <= 0) {
            throw new IllegalArgumentException(
                    "Valid start time and target are required");
        }

        return startTime.plusMinutes(targetMinutes);
    }

    // 7. Calculate SLA status
    public SlaStatusResponse calculateSlaStatus(
            LocalDateTime startTime,
            LocalDateTime dueTime,
            LocalDateTime completedTime,
            Integer warningThresholdPercent) {

        if (startTime == null || dueTime == null
                || dueTime.isBefore(startTime)) {
            throw new IllegalArgumentException(
                    "Valid start and due times are required");
        }

        if (warningThresholdPercent == null
                || warningThresholdPercent < 1
                || warningThresholdPercent > 99) {
            throw new IllegalArgumentException(
                    "Warning threshold must be between 1 and 99");
        }

        LocalDateTime now = completedTime != null
                ? completedTime
                : LocalDateTime.now();

        long totalMinutes =
                Duration.between(startTime, dueTime).toMinutes();

        long elapsedMinutes =
                Duration.between(startTime, now).toMinutes();

        double percentage = totalMinutes == 0
                ? 100.0
                : elapsedMinutes * 100.0 / totalMinutes;

        long remainingMinutes = Math.max(
                0,
                Duration.between(now, dueTime).toMinutes());

        String status;
        String message;

        if (now.isAfter(dueTime)) {
            status = "BREACHED";
            message = "SLA deadline exceeded.";
        } else if (completedTime != null) {
            status = "ON_TRACK";
            message = "Completed within SLA.";
        } else if (percentage >= warningThresholdPercent) {
            status = "AT_RISK";
            message = "SLA deadline approaching.";
        } else {
            status = "ON_TRACK";
            message = "SLA is within permitted time.";
        }

        return new SlaStatusResponse(
                status,
                remainingMinutes,
                Math.round(percentage * 100.0) / 100.0,
                message);
    }

    // Monitor ticket response or resolution SLA
    public SlaStatusResponse getTicketSlaStatus(
            Long ticketId,
            String type) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Ticket not found: " + ticketId));

        SlaPolicy policy = getPolicyByPriority(ticket.getPriority());

        LocalDateTime startTime;
        LocalDateTime dueTime;
        LocalDateTime completedTime;

        if ("RESPONSE".equalsIgnoreCase(type)) {

            startTime = ticket.getCreatedAt();
            dueTime = ticket.getResponseDueAt();
            completedTime = ticket.getFirstRespondedAt();

        } else if ("RESOLUTION".equalsIgnoreCase(type)) {

            startTime = ticket.getCreatedAt();
            dueTime = ticket.getResolutionDueAt();
            completedTime = ticket.getResolvedAt();

        } else {
            throw new IllegalArgumentException(
                    "Type must be RESPONSE or RESOLUTION");
        }

        return calculateSlaStatus(
                startTime,
                dueTime,
                completedTime,
                policy.getWarningThresholdPercent()
        );
    }

}
