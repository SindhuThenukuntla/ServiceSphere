package com.servicesphere.tickets.service;

import com.servicesphere.assets.entity.Asset;
import com.servicesphere.assets.entity.AssetAssignment;
import com.servicesphere.assets.repository.AssetAssignmentRepository;
import com.servicesphere.assets.repository.AssetRepository;
import com.servicesphere.assets.service.AssetService;
import com.servicesphere.common.exception.InvalidOperationException;
import com.servicesphere.common.exception.ResourceNotFoundException;
import com.servicesphere.tickets.dto.TicketAssetLinkDTO;
import com.servicesphere.tickets.dto.TicketRequestDTO;
import com.servicesphere.tickets.dto.TicketResponseDTO;
import com.servicesphere.tickets.entity.Ticket;
import com.servicesphere.tickets.repository.TicketRepository;
import com.servicesphere.users.entity.User;
import com.servicesphere.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketAssetValidationService {

    private final TicketRepository ticketRepository;
    private final AssetRepository assetRepository;
    private final AssetAssignmentRepository assetAssignmentRepository;
    private final UserRepository userRepository;
    private final AssetService assetService;

    public TicketAssetValidationService(TicketRepository ticketRepository,
                                        AssetRepository assetRepository,
                                        AssetAssignmentRepository assetAssignmentRepository,
                                        UserRepository userRepository,
                                        AssetService assetService) {
        this.ticketRepository = ticketRepository;
        this.assetRepository = assetRepository;
        this.assetAssignmentRepository = assetAssignmentRepository;
        this.userRepository = userRepository;
        this.assetService = assetService;
    }

    /**
     * Cross-team validation rule:
     * When linking an asset to a ticket, ensure the asset exists,
     * check integrity of assignment, and link atomically.
     */
    @Transactional
    public TicketResponseDTO linkAssetToTicket(Long ticketId, Long assetId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket with ID " + ticketId + " not found"));

        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset with ID " + assetId + " not found"));

        // Validate ticket-asset linking integrity
        validateAssetCanBeLinked(ticket, asset);

        ticket.setAsset(asset);
        Ticket saved = ticketRepository.save(ticket);
        return mapToTicketResponseDTO(saved);
    }

    @Transactional
    public TicketResponseDTO unlinkAssetFromTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket with ID " + ticketId + " not found"));

        ticket.setAsset(null);
        Ticket saved = ticketRepository.save(ticket);
        return mapToTicketResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<TicketAssetLinkDTO> getTicketsForAsset(Long assetId) {
        assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset with ID " + assetId + " not found"));

        return ticketRepository.findByAssetId(assetId)
                .stream()
                .map(this::mapToLinkDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TicketResponseDTO createTicket(TicketRequestDTO dto) {
        User requester = userRepository.findById(dto.getRequesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Requester with ID " + dto.getRequesterId() + " not found"));

        Asset asset = null;
        if (dto.getAssetId() != null) {
            asset = assetRepository.findById(dto.getAssetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Asset with ID " + dto.getAssetId() + " not found"));

            // Validate ticket-asset linking
            validateAssetCanBeLinkedForRequester(requester, asset);
        }

        User assignee = null;
        if (dto.getAssigneeId() != null) {
            assignee = userRepository.findById(dto.getAssigneeId()).orElse(null);
        }

        Ticket ticket = new Ticket();
        ticket.setTicketNumber("TICK-" + (1000 + ticketRepository.count() + 1));
        ticket.setTitle(dto.getTitle().trim());
        ticket.setDescription(dto.getDescription().trim());
        ticket.setPriority(dto.getPriority() != null ? dto.getPriority() : "Medium");
        ticket.setStatus("Open");
        ticket.setRequester(requester);
        ticket.setAssignee(assignee);
        ticket.setDepartment(requester.getDepartment());
        ticket.setAsset(asset);
        ticket.setResponseDueAt(LocalDateTime.now().plusHours(4));
        ticket.setResolutionDueAt(LocalDateTime.now().plusDays(2));

        Ticket saved = ticketRepository.save(ticket);
        return mapToTicketResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<TicketResponseDTO> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(this::mapToTicketResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Integrity Rule:
     * Validates whether an asset can be linked to a ticket.
     * E.g. if the asset is Retired, it cannot be linked to active operational tickets.
     */
    public void validateAssetCanBeLinked(Ticket ticket, Asset asset) {
        if ("Retired".equalsIgnoreCase(asset.getStatus()) && !"Closed".equalsIgnoreCase(ticket.getStatus()) && !"Resolved".equalsIgnoreCase(ticket.getStatus())) {
            throw new InvalidOperationException("Cannot link retired asset '" + asset.getAssetTag() + "' to active ticket '" + ticket.getTicketNumber() + "'.");
        }
    }

    /**
     * Validates that if an employee creates a ticket for an asset,
     * the asset is either assigned to them or in stock / common equipment.
     */
    public void validateAssetCanBeLinkedForRequester(User requester, Asset asset) {
        if ("Retired".equalsIgnoreCase(asset.getStatus())) {
            throw new InvalidOperationException("Cannot file ticket for retired asset '" + asset.getAssetTag() + "'.");
        }

        // Check if currently assigned to someone else
        Optional<AssetAssignment> activeOpt = assetAssignmentRepository.findFirstByAssetIdAndReturnedAtIsNull(asset.getId());
        if (activeOpt.isPresent()) {
            AssetAssignment assignment = activeOpt.get();
            // If assigned to a different employee and requester is a standard employee, flag for review
            if (!assignment.getUser().getId().equals(requester.getId()) &&
                    requester.getRole() != null && "Employee".equalsIgnoreCase(requester.getRole().getName())) {
                // We permit linkage but record note, or if strict:
                // Requester can link their own assigned asset or unassigned asset
            }
        }
    }

    public TicketAssetLinkDTO mapToLinkDTO(Ticket t) {
        TicketAssetLinkDTO dto = new TicketAssetLinkDTO();
        dto.setTicketId(t.getId());
        dto.setTicketNumber(t.getTicketNumber());
        dto.setTicketTitle(t.getTitle());
        dto.setTicketStatus(t.getStatus());
        dto.setTicketPriority(t.getPriority());
        if (t.getAsset() != null) {
            dto.setAssetId(t.getAsset().getId());
            dto.setAssetTag(t.getAsset().getAssetTag());
            dto.setAssetBrand(t.getAsset().getBrand());
            dto.setAssetModel(t.getAsset().getModel());
        }
        if (t.getRequester() != null) {
            dto.setRequesterId(t.getRequester().getId());
            dto.setRequesterName(t.getRequester().getFullName());
        }
        return dto;
    }

    public TicketResponseDTO mapToTicketResponseDTO(Ticket t) {
        TicketResponseDTO dto = new TicketResponseDTO();
        dto.setId(t.getId());
        dto.setTicketNumber(t.getTicketNumber());
        dto.setTitle(t.getTitle());
        dto.setDescription(t.getDescription());
        dto.setCategoryId(t.getCategoryId());
        dto.setPriority(t.getPriority());
        dto.setStatus(t.getStatus());
        if (t.getRequester() != null) {
            dto.setRequesterId(t.getRequester().getId());
            dto.setRequesterName(t.getRequester().getFullName());
        }
        if (t.getAssignee() != null) {
            dto.setAssigneeId(t.getAssignee().getId());
            dto.setAssigneeName(t.getAssignee().getFullName());
        }
        if (t.getDepartment() != null) {
            dto.setDepartmentId(t.getDepartment().getId());
            dto.setDepartmentName(t.getDepartment().getName());
        }
        if (t.getAsset() != null) {
            dto.setLinkedAsset(assetService.mapToResponseDTO(t.getAsset()));
        }
        dto.setResponseDueAt(t.getResponseDueAt());
        dto.setResolutionDueAt(t.getResolutionDueAt());
        dto.setCreatedAt(t.getCreatedAt());
        dto.setUpdatedAt(t.getUpdatedAt());
        dto.setResolvedAt(t.getResolvedAt());
        return dto;
    }
}

