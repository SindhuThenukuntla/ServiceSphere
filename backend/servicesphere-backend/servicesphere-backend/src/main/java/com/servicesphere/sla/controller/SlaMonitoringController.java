package com.servicesphere.sla.controller;

import com.servicesphere.sla.dto.SlaStatusResponse;
import com.servicesphere.sla.service.SlaService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sla/tickets")
public class SlaMonitoringController {

    private final SlaService slaService;

    public SlaMonitoringController(SlaService slaService) {
        this.slaService = slaService;
    }

    // Check ticket SLA status
    @GetMapping("/{ticketId}/status")
    public SlaStatusResponse getTicketSlaStatus(
            @PathVariable Long ticketId,
            @RequestParam(defaultValue = "RESOLUTION") String type) {

        return slaService.getTicketSlaStatus(ticketId, type);
    }
}
