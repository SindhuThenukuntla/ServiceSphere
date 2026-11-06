import { apiRequest } from "./api";

const notificationService = {
  // Get all notifications for a user
  getUserNotifications(recipientId) {
    return apiRequest(`/notifications/user/${recipientId}`);
  },

  // Get unread notifications
  getUnreadNotifications(recipientId) {
    return apiRequest(`/notifications/user/${recipientId}/unread`);
  },

  // Get unread notification count
  getUnreadCount(recipientId) {
    return apiRequest(`/notifications/user/${recipientId}/unread/count`);
  },

  // Filter notifications by type
  getNotificationsByType(recipientId, type) {
    return apiRequest(`/notifications/user/${recipientId}/type/${type}`);
  },

  // Mark one notification as read
  markAsRead(notificationId, recipientId) {
    return apiRequest(
      `/notifications/${notificationId}/read?recipientId=${recipientId}`,
      { method: "PUT" }
    );
  },

  // Mark all notifications as read
  markAllAsRead(recipientId) {
    return apiRequest(
      `/notifications/user/${recipientId}/read-all`,
      { method: "PUT" }
    );
  },

  // Delete all notifications
  deleteUserNotifications(recipientId) {
    return apiRequest(`/notifications/user/${recipientId}`, {
      method: "DELETE",
    });
  },

  // Create notification
  createNotification(notificationData) {
    return apiRequest("/notifications", {
      method: "POST",
      body: JSON.stringify(notificationData),
    });
  },
};

export default notificationService;
