
package com.servicesphere.notification.repository;

import com.servicesphere.notification.entity.Notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    // Get all notifications for a user
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(
            Long recipientId
    );

    // Get unread notifications
    List<Notification> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(
            Long recipientId
    );

    // Count unread notifications
    long countByRecipientIdAndIsReadFalse(Long recipientId);

    // Get notifications by type
    List<Notification> findByRecipientIdAndTypeOrderByCreatedAtDesc(
            Long recipientId,
            String type
    );

    // Get notifications related to a ticket
    List<Notification> findByTicketId(Long ticketId);

    // Delete notifications for a user
    void deleteByRecipientId(Long recipientId);

    // Check whether an SLA notification already exists
    boolean existsByTicketIdAndType(Long ticketId, String type);
}
