
package com.servicesphere.escalation.repository;

import com.servicesphere.escalation.entity.Escalation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EscalationRepository
        extends JpaRepository<Escalation, Long> {

    // Find all escalations for a ticket
    List<Escalation> findByTicketId(Long ticketId);

    // Find escalations performed by a particular user
    List<Escalation> findByEscalatedBy(Long escalatedBy);

    // Find escalations by status
    List<Escalation> findByStatus(String status);

    // Find escalation history by ticket and level
    List<Escalation> findByTicketIdAndEscalationLevel(
            Long ticketId,
            Integer escalationLevel
    );

    // Find latest escalation history
    List<Escalation> findByTicketIdOrderByEscalatedAtDesc(
            Long ticketId
    );

    // Find escalations assigned to a particular agent
    List<Escalation> findByNewAssigneeId(Long newAssigneeId);

    // Check whether a ticket has already reached an escalation level
    boolean existsByTicketIdAndEscalationLevel(
            Long ticketId,
            Integer escalationLevel
    );

    // Get the latest escalation level for a ticket
    Escalation findTopByTicketIdOrderByEscalationLevelDesc(
            Long ticketId
    );
}
