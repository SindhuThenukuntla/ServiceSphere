
package com.servicesphere.notification.service;

import com.servicesphere.notification.entity.Notification;
import com.servicesphere.notification.repository.NotificationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(
            NotificationRepository notificationRepository) {

        this.notificationRepository = notificationRepository;
    }

    // Create notification
    public Notification createNotification(
            Long recipientId,
            Long ticketId,
            String type,
            String title,
            String message) {

        if (recipientId == null || type == null || type.isBlank()
                || title == null || title.isBlank()
                || message == null || message.isBlank()) {

            throw new IllegalArgumentException(
                    "Recipient, type, title and message are required");
        }

        Notification notification = new Notification();

        notification.setRecipientId(recipientId);
        notification.setTicketId(ticketId);
        notification.setType(type.toUpperCase());
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setIsRead(false);

        return notificationRepository.save(notification);
    }

    // Get all notifications for a user
    public List<Notification> getUserNotifications(Long recipientId) {
        return notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(recipientId);
    }

    // Get unread notifications
    public List<Notification> getUnreadNotifications(Long recipientId) {
        return notificationRepository
                .findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(
                        recipientId);
    }

    // Get unread notification count
    public long getUnreadCount(Long recipientId) {
        return notificationRepository
                .countByRecipientIdAndIsReadFalse(recipientId);
    }

    // Get notifications by type
    public List<Notification> getNotificationsByType(
            Long recipientId,
            String type) {

        return notificationRepository
                .findByRecipientIdAndTypeOrderByCreatedAtDesc(
                        recipientId, type.toUpperCase());
    }

    // Mark one notification as read
    @Transactional
    public Notification markAsRead(Long notificationId, Long recipientId) {

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Notification not found: " + notificationId));

        if (!notification.getRecipientId().equals(recipientId)) {
            throw new RuntimeException(
                    "You cannot access another user's notification");
        }

        if (!Boolean.TRUE.equals(notification.getIsRead())) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
        }

        return notificationRepository.save(notification);
    }

    // Mark all notifications as read
    @Transactional
    public int markAllAsRead(Long recipientId) {

        List<Notification> notifications =
                notificationRepository
                        .findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(
                                recipientId);

        LocalDateTime now = LocalDateTime.now();

        for (Notification notification : notifications) {
            notification.setIsRead(true);
            notification.setReadAt(now);
        }

        notificationRepository.saveAll(notifications);

        return notifications.size();
    }

    // Delete all notifications for a user
    @Transactional
    public void deleteUserNotifications(Long recipientId) {
        notificationRepository.deleteByRecipientId(recipientId);
    }
}
