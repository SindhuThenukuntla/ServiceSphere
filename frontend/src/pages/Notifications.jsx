import { useState } from "react";
import notificationService from "../services/notificationService";
import "../styles/Notifications.css";

function Notifications() {
  const [recipientId, setRecipientId] = useState("1");
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [filter, setFilter] = useState("ALL");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const loadNotifications = async (selectedFilter = filter) => {
    if (!recipientId || Number(recipientId) <= 0) {
      setMessage("Please enter a valid user ID.");
      return;
    }

    try {
      setLoading(true);
      setMessage("");

      let data;

      if (selectedFilter === "UNREAD") {
        data = await notificationService.getUnreadNotifications(recipientId);
      } else if (selectedFilter === "ALL") {
        data = await notificationService.getUserNotifications(recipientId);
      } else {
        data = await notificationService.getNotificationsByType(
          recipientId,
          selectedFilter
        );
      }

      setNotifications(Array.isArray(data) ? data : []);

      const count = await notificationService.getUnreadCount(recipientId);
      setUnreadCount(Number(count) || 0);
    } catch (error) {
      setMessage(error.message || "Unable to load notifications.");
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (value) => {
    setFilter(value);
    loadNotifications(value);
  };

  const handleMarkAsRead = async (notificationId) => {
    try {
      await notificationService.markAsRead(notificationId, recipientId);
      setMessage("Notification marked as read.");
      await loadNotifications();
    } catch (error) {
      setMessage(error.message || "Unable to update notification.");
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await notificationService.markAllAsRead(recipientId);
      setMessage("All notifications marked as read.");
      await loadNotifications();
    } catch (error) {
      setMessage(error.message || "Unable to update notifications.");
    }
  };

  const handleDeleteAll = async () => {
    const confirmed = window.confirm(
      "Are you sure you want to delete all notifications?"
    );

    if (!confirmed) return;

    try {
      await notificationService.deleteUserNotifications(recipientId);
      setNotifications([]);
      setUnreadCount(0);
      setMessage("All notifications deleted.");
    } catch (error) {
      setMessage(error.message || "Unable to delete notifications.");
    }
  };

  return (
    <div className="notifications-page">
      <div className="notifications-heading">
        <div>
          <h2>Notifications</h2>
          <p>View and manage your support notifications.</p>
        </div>

        <div className="unread-summary">
          <span>Unread Notifications</span>
          <strong>{unreadCount}</strong>
        </div>
      </div>

      {message && <div className="notification-message">{message}</div>}

      <section className="notification-controls">
        <div className="recipient-control">
          <label htmlFor="recipientId">Recipient User ID</label>
          <input
            id="recipientId"
            type="number"
            min="1"
            value={recipientId}
            onChange={(e) => setRecipientId(e.target.value)}
            placeholder="Enter user ID"
          />
          <button onClick={() => loadNotifications("ALL")}>
            Load Notifications
          </button>
        </div>

        <div className="notification-actions">
          <button
            className="mark-all-button"
            onClick={handleMarkAllAsRead}
            disabled={!notifications.length || unreadCount === 0}
          >
            Mark All as Read
          </button>

          <button
            className="delete-all-button"
            onClick={handleDeleteAll}
            disabled={!notifications.length}
          >
            Delete All
          </button>
        </div>
      </section>

      <section className="notification-list-section">
        <div className="notification-list-header">
          <h3>Notification History</h3>

          <select
            value={filter}
            onChange={(e) => handleFilterChange(e.target.value)}
          >
            <option value="ALL">All Notifications</option>
            <option value="UNREAD">Unread Notifications</option>
            <option value="TICKET_ASSIGNED">Ticket Assigned</option>
            <option value="ESCALATION">Escalation</option>
            <option value="SLA_WARNING">SLA Warning</option>
            <option value="SLA_BREACH">SLA Breach</option>
          </select>
        </div>

        {loading ? (
          <p className="notification-empty">Loading notifications...</p>
        ) : notifications.length === 0 ? (
          <p className="notification-empty">No notifications found.</p>
        ) : (
          <div className="notification-list">
            {notifications.map((item) => (
              <article
                className={`notification-card ${
                  item.isRead ? "read" : "unread"
                }`}
                key={item.id}
              >
                <div className="notification-card-content">
                  <div className="notification-card-top">
                    <h4>{item.title}</h4>
                    <span className="notification-type">{item.type}</span>
                  </div>

                  <p>{item.message}</p>

                  <div className="notification-meta">
                    <span>Notification ID: {item.id}</span>
                    <span>Ticket ID: {item.ticketId ?? "N/A"}</span>
                    <span>
                      {item.createdAt
                        ? new Date(item.createdAt).toLocaleString()
                        : ""}
                    </span>
                    <span>{item.isRead ? "Read" : "Unread"}</span>
                  </div>
                </div>

                {!item.isRead && (
                  <button
                    className="mark-read-button"
                    onClick={() => handleMarkAsRead(item.id)}
                  >
                    Mark as Read
                  </button>
                )}
              </article>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}

export default Notifications;
