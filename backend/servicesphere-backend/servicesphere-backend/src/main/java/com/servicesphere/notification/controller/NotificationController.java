package com.servicesphere.notification.controller;

import com.servicesphere.notification.dto.NotificationRequest;
import com.servicesphere.notification.entity.Notification;
import com.servicesphere.notification.service.NotificationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Create notification
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Notification createNotification(
            @Valid @RequestBody NotificationRequest request) {

        return notificationService.createNotification(
                request.getRecipientId(),
                request.getTicketId(),
                request.getType(),
                request.getTitle(),
                request.getMessage()
        );
    }

    // Get all notifications for a user
    @GetMapping("/user/{recipientId}")
    public List<Notification> getUserNotifications(
            @PathVariable Long recipientId) {

        return notificationService.getUserNotifications(recipientId);
    }

    // Get unread notifications
    @GetMapping("/user/{recipientId}/unread")
    public List<Notification> getUnreadNotifications(
            @PathVariable Long recipientId) {

        return notificationService.getUnreadNotifications(recipientId);
    }

    // Get unread notification count
    @GetMapping("/user/{recipientId}/unread/count")
    public long getUnreadCount(
            @PathVariable Long recipientId) {

        return notificationService.getUnreadCount(recipientId);
    }

    // Filter notifications by type
    @GetMapping("/user/{recipientId}/type/{type}")
    public List<Notification> getNotificationsByType(
            @PathVariable Long recipientId,
            @PathVariable String type) {

        return notificationService.getNotificationsByType(
                recipientId, type);
    }

    // Mark one notification as read
    @PutMapping("/{notificationId}/read")
    public Notification markAsRead(
            @PathVariable Long notificationId,
            @RequestParam Long recipientId) {

        return notificationService.markAsRead(
                notificationId, recipientId);
    }

    // Mark all notifications as read
    @PutMapping("/user/{recipientId}/read-all")
    public int markAllAsRead(
            @PathVariable Long recipientId) {

        return notificationService.markAllAsRead(recipientId);
    }

    // Delete all notifications for a user
    @DeleteMapping("/user/{recipientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserNotifications(
            @PathVariable Long recipientId) {

        notificationService.deleteUserNotifications(recipientId);
    }
}
