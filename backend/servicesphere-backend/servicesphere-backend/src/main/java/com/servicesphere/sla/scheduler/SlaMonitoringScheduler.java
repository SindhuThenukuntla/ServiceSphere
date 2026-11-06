
package com.servicesphere.sla.scheduler;

import com.servicesphere.sla.dto.SlaStatusResponse;
import com.servicesphere.sla.service.SlaService;

import com.servicesphere.ticket.entity.Ticket;
import com.servicesphere.ticket.repository.TicketRepository;

import com.servicesphere.notification.repository.NotificationRepository;
import com.servicesphere.notification.service.NotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SlaMonitoringScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(SlaMonitoringScheduler.class);

    private final TicketRepository ticketRepository;
    private final SlaService slaService;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    public SlaMonitoringScheduler(
            TicketRepository ticketRepository,
            SlaService slaService,
            NotificationService notificationService,
            NotificationRepository notificationRepository) {

        this.ticketRepository = ticketRepository;
        this.slaService = slaService;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }

    // Check all tickets every minute
    @Scheduled(
            fixedDelayString = "${servicesphere.sla.monitor-interval-ms:60000}",
            initialDelayString = "${servicesphere.sla.initial-delay-ms:10000}"
    )
    public void monitorTickets() {

        log.info("Starting automatic SLA monitoring");

        List<Ticket> tickets = ticketRepository.findAll();

        for (Ticket ticket : tickets) {

            // Notifications require an assigned agent
            if (ticket.getAssigneeId() == null) {
                continue;
            }

            // Check response SLA
            checkSla(ticket, "RESPONSE");

            // Check resolution SLA
            checkSla(ticket, "RESOLUTION");
        }

        log.info("SLA monitoring completed");
    }

    private void checkSla(Ticket ticket, String type) {

        try {

            LocalDateTime dueTime;

            if ("RESPONSE".equals(type)) {
                dueTime = ticket.getResponseDueAt();
            } else {
                dueTime = ticket.getResolutionDueAt();
            }

            if (dueTime == null) {
                return;
            }

            SlaStatusResponse status =
                    slaService.getTicketSlaStatus(
                            ticket.getId(), type);

            String notificationType = null;
            String title = null;
            String message = null;

            if ("AT_RISK".equals(status.getStatus())) {

                notificationType = "SLA_" + type + "_WARNING";

                title = "SLA deadline approaching";

                message = "Ticket " + ticket.getTicketNumber()
                        + " is approaching its " + type
                        + " SLA deadline. Remaining time: "
                        + status.getRemainingMinutes() + " minutes.";

            } else if ("BREACHED".equals(status.getStatus())) {

                notificationType = "SLA_" + type + "_BREACH";

                title = "SLA deadline breached";

                message = "Ticket " + ticket.getTicketNumber()
                        + " has exceeded its " + type
                        + " SLA deadline.";
            }

            if (notificationType == null) {
                return;
            }

            // Prevent duplicate notifications
            boolean alreadyExists =
                    notificationRepository.existsByTicketIdAndType(
                            ticket.getId(), notificationType);

            if (alreadyExists) {
                return;
            }

            // Create automatic notification
            notificationService.createNotification(
                    ticket.getAssigneeId(),
                    ticket.getId(),
                    notificationType,
                    title,
                    message
            );

            log.info(
                    "SLA notification created: ticket={}, type={}",
                    ticket.getTicketNumber(),
                    notificationType
            );

        } catch (Exception exception) {

            log.error(
                    "SLA monitoring failed for ticket {} and type {}",
                    ticket.getId(),
                    type,
                    exception
            );
        }
    }
}
