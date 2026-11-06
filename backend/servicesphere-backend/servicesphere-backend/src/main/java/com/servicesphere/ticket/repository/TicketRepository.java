
package com.servicesphere.ticket.repository;

import com.servicesphere.ticket.entity.Ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Find tickets by status
    List<Ticket> findByStatus(String status);

    // Find tickets by priority
    List<Ticket> findByPriority(String priority);

    // Find tickets assigned to an agent
    List<Ticket> findByAssigneeId(Long assigneeId);

    // Find tickets created by a requester
    List<Ticket> findByRequesterId(Long requesterId);

    // Find unassigned tickets
    List<Ticket> findByAssigneeIdIsNull();

    // Find tickets by priority and status
    List<Ticket> findByPriorityAndStatus(
            String priority, String status);

    // Find high and critical priority tickets
    List<Ticket> findByPriorityIn(List<String> priorities);

    // Find overdue unresolved tickets
    List<Ticket> findByResolutionDueAtBeforeAndResolvedAtIsNull(
            LocalDateTime currentTime);

    // Find recently updated tickets
    List<Ticket> findAllByOrderByUpdatedAtDesc();

    // Find tickets assigned to an agent by status
    List<Ticket> findByAssigneeIdAndStatus(
            Long assigneeId, String status);

    // Count tickets assigned to an agent
    long countByAssigneeId(Long assigneeId);

    // Count tickets assigned to an agent by status
    long countByAssigneeIdAndStatus(
            Long assigneeId,
            String status
    );
}
