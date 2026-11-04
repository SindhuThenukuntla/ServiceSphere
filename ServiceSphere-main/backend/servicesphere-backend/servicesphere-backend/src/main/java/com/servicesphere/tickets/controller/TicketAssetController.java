package com.servicesphere.tickets.controller;

import com.servicesphere.tickets.dto.TicketAssetLinkDTO;
import com.servicesphere.tickets.dto.TicketRequestDTO;
import com.servicesphere.tickets.dto.TicketResponseDTO;
import com.servicesphere.tickets.service.TicketAssetValidationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class TicketAssetController {

    private final TicketAssetValidationService ticketAssetValidationService;

    public TicketAssetController(TicketAssetValidationService ticketAssetValidationService) {
        this.ticketAssetValidationService = ticketAssetValidationService;
    }

    @GetMapping("/api/assets/{assetId}/tickets")
    public ResponseEntity<List<TicketAssetLinkDTO>> getTicketsForAsset(@PathVariable Long assetId) {
        return ResponseEntity.ok(ticketAssetValidationService.getTicketsForAsset(assetId));
    }

    @PostMapping("/api/tickets/{ticketId}/link-asset/{assetId}")
    public ResponseEntity<TicketResponseDTO> linkAsset(
            @PathVariable Long ticketId,
            @PathVariable Long assetId) {
        return ResponseEntity.ok(ticketAssetValidationService.linkAssetToTicket(ticketId, assetId));
    }

    @DeleteMapping("/api/tickets/{ticketId}/unlink-asset")
    public ResponseEntity<TicketResponseDTO> unlinkAsset(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ticketAssetValidationService.unlinkAssetFromTicket(ticketId));
    }

    @PostMapping("/api/tickets")
    public ResponseEntity<TicketResponseDTO> createTicket(@Valid @RequestBody TicketRequestDTO request) {
        TicketResponseDTO created = ticketAssetValidationService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/api/tickets")
    public ResponseEntity<List<TicketResponseDTO>> getAllTickets() {
        return ResponseEntity.ok(ticketAssetValidationService.getAllTickets());
    }
}

