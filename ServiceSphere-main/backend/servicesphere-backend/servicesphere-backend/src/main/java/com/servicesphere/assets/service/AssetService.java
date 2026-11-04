package com.servicesphere.assets.service;

import com.servicesphere.assets.dto.*;
import com.servicesphere.assets.entity.Asset;
import com.servicesphere.assets.entity.AssetAssignment;
import com.servicesphere.assets.repository.AssetAssignmentRepository;
import com.servicesphere.assets.repository.AssetRepository;
import com.servicesphere.common.exception.ConflictException;
import com.servicesphere.common.exception.InvalidOperationException;
import com.servicesphere.common.exception.ResourceNotFoundException;
import com.servicesphere.users.entity.User;
import com.servicesphere.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssetService {

    private final AssetRepository assetRepository;
    private final AssetAssignmentRepository assetAssignmentRepository;
    private final UserRepository userRepository;

    public AssetService(AssetRepository assetRepository,
                        AssetAssignmentRepository assetAssignmentRepository,
                        UserRepository userRepository) {
        this.assetRepository = assetRepository;
        this.assetAssignmentRepository = assetAssignmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<AssetResponseDTO> getAssets(String type, String status, String search, Pageable pageable) {
        String cleanType = (type != null && !type.isBlank()) ? type.trim() : null;
        String cleanStatus = (status != null && !status.isBlank()) ? status.trim() : null;
        String cleanSearch = (search != null && !search.isBlank()) ? search.trim() : null;

        return assetRepository.findByFilters(cleanType, cleanStatus, cleanSearch, pageable)
                .map(this::mapToResponseDTO);
    }

    @Transactional(readOnly = true)
    public AssetResponseDTO getAssetById(Long id) {
        Asset asset = findAssetOrThrow(id);
        return mapToResponseDTO(asset);
    }

    @Transactional
    public AssetResponseDTO createAsset(AssetRequestDTO request) {
        if (assetRepository.existsByAssetTag(request.getAssetTag().trim())) {
            throw new ConflictException("Asset with tag '" + request.getAssetTag() + "' already exists");
        }

        Asset asset = new Asset();
        asset.setAssetTag(request.getAssetTag().trim().toUpperCase());
        asset.setAssetType(request.getAssetType());
        asset.setBrand(request.getBrand().trim());
        asset.setModel(request.getModel().trim());
        asset.setSerialNumber(request.getSerialNumber() != null ? request.getSerialNumber().trim() : null);
        asset.setStatus(request.getStatus() != null ? request.getStatus() : "In Stock");
        asset.setPurchaseDate(request.getPurchaseDate());
        asset.setWarrantyEndDate(request.getWarrantyEndDate());
        asset.setLocation(request.getLocation() != null ? request.getLocation().trim() : null);
        asset.setNotes(request.getNotes());

        Asset saved = assetRepository.save(asset);
        return mapToResponseDTO(saved);
    }

    @Transactional
    public AssetResponseDTO updateAsset(Long id, Map<String, Object> updates) {
        Asset asset = findAssetOrThrow(id);

        if (updates.containsKey("assetTag")) {
            String newTag = ((String) updates.get("assetTag")).trim().toUpperCase();
            if (!newTag.equalsIgnoreCase(asset.getAssetTag()) && assetRepository.existsByAssetTagAndIdNot(newTag, id)) {
                throw new ConflictException("Asset tag '" + newTag + "' is already in use by another asset");
            }
            asset.setAssetTag(newTag);
        }

        if (updates.containsKey("brand")) {
            asset.setBrand(((String) updates.get("brand")).trim());
        }
        if (updates.containsKey("model")) {
            asset.setModel(((String) updates.get("model")).trim());
        }
        if (updates.containsKey("serialNumber")) {
            asset.setSerialNumber((String) updates.get("serialNumber"));
        }
        if (updates.containsKey("location")) {
            asset.setLocation((String) updates.get("location"));
        }
        if (updates.containsKey("notes")) {
            asset.setNotes((String) updates.get("notes"));
        }
        if (updates.containsKey("assetType")) {
            asset.setAssetType((String) updates.get("assetType"));
        }
        if (updates.containsKey("purchaseDate") && updates.get("purchaseDate") != null) {
            asset.setPurchaseDate(LocalDate.parse(updates.get("purchaseDate").toString()));
        }
        if (updates.containsKey("warrantyEndDate") && updates.get("warrantyEndDate") != null) {
            asset.setWarrantyEndDate(LocalDate.parse(updates.get("warrantyEndDate").toString()));
        }

        if (updates.containsKey("status")) {
            String newStatus = (String) updates.get("status");
            validateStatusTransition(asset, newStatus);
            asset.setStatus(newStatus);
        }

        Asset saved = assetRepository.save(asset);
        return mapToResponseDTO(saved);
    }

    @Transactional
    public AssetResponseDTO assignAsset(Long assetId, AssetAssignRequestDTO request) {
        Asset asset = findAssetOrThrow(assetId);

        // Rule: Prevent assignment of retired or in-repair assets
        if ("Retired".equalsIgnoreCase(asset.getStatus())) {
            throw new InvalidOperationException("Cannot assign asset '" + asset.getAssetTag() + "' because it is Retired.");
        }
        if ("In Repair".equalsIgnoreCase(asset.getStatus())) {
            throw new InvalidOperationException("Cannot assign asset '" + asset.getAssetTag() + "' because it is currently In Repair.");
        }

        // Rule: An asset may have many historical assignments but no more than one active assignment at a time
        if (assetAssignmentRepository.existsByAssetIdAndReturnedAtIsNull(assetId) || "Assigned".equalsIgnoreCase(asset.getStatus())) {
            throw new ConflictException("Asset '" + asset.getAssetTag() + "' already has an active assignment. Please return it first.");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee with ID " + request.getUserId() + " not found"));

        User assigner = null;
        if (request.getAssignedBy() != null) {
            assigner = userRepository.findById(request.getAssignedBy()).orElse(null);
        }
        if (assigner == null) {
            // Default assigner to user itself or first admin if none provided
            assigner = user;
        }

        LocalDateTime assignedTime = request.getAssignedAt() != null ? request.getAssignedAt() : LocalDateTime.now();

        // Multi-step atomic transaction:
        // 1. Create active assignment record
        AssetAssignment assignment = new AssetAssignment();
        assignment.setAsset(asset);
        assignment.setUser(user);
        assignment.setAssignedAt(assignedTime);
        assignment.setConditionOut(request.getConditionOut().trim());
        assignment.setAssignedBy(assigner);

        assetAssignmentRepository.save(assignment);

        // 2. Transition asset status to Assigned
        asset.setStatus("Assigned");
        Asset savedAsset = assetRepository.save(asset);

        return mapToResponseDTO(savedAsset);
    }

    @Transactional
    public AssetResponseDTO returnAsset(Long assetId, AssetReturnRequestDTO request) {
        Asset asset = findAssetOrThrow(assetId);

        // Find active assignment
        Optional<AssetAssignment> activeOpt = assetAssignmentRepository.findFirstByAssetIdAndReturnedAtIsNull(assetId);
        if (activeOpt.isEmpty()) {
            throw new InvalidOperationException("Asset '" + asset.getAssetTag() + "' does not have an active assignment to return.");
        }

        AssetAssignment activeAssignment = activeOpt.get();
        LocalDateTime returnTime = request.getReturnedAt() != null ? request.getReturnedAt() : LocalDateTime.now();

        // 1. Close active assignment
        activeAssignment.setReturnedAt(returnTime);
        activeAssignment.setConditionIn(request.getConditionIn().trim());
        assetAssignmentRepository.save(activeAssignment);

        // 2. Update asset status
        String nextStatus = request.getNextStatus() != null ? request.getNextStatus() : "In Stock";
        if (!Arrays.asList("In Stock", "In Repair", "Retired").contains(nextStatus)) {
            nextStatus = "In Stock";
        }
        asset.setStatus(nextStatus);
        Asset savedAsset = assetRepository.save(asset);

        return mapToResponseDTO(savedAsset);
    }

    @Transactional(readOnly = true)
    public List<AssetAssignmentHistoryDTO> getAssetAssignmentHistory(Long assetId) {
        findAssetOrThrow(assetId);
        return assetAssignmentRepository.findByAssetIdOrderByAssignedAtDesc(assetId)
                .stream()
                .map(this::mapToHistoryDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AssetResponseDTO> getAssignedAssetsForUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

        return assetAssignmentRepository.findActiveAssignmentsForUser(userId)
                .stream()
                .map(aa -> mapToResponseDTO(aa.getAsset()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AssetSummaryDTO getAssetSummary() {
        AssetSummaryDTO summary = new AssetSummaryDTO();
        long total = assetRepository.count();
        summary.setTotalAssets(total);
        summary.setInStockCount(assetRepository.countByStatus("In Stock"));
        summary.setAssignedCount(assetRepository.countByStatus("Assigned"));
        summary.setInRepairCount(assetRepository.countByStatus("In Repair"));
        summary.setRetiredCount(assetRepository.countByStatus("Retired"));

        LocalDate today = LocalDate.now();
        LocalDate in30Days = today.plusDays(30);

        List<Asset> expiring = assetRepository.findAssetsWithUpcomingOrExpiredWarranty(in30Days);
        long expired = expiring.stream().filter(a -> a.getWarrantyEndDate().isBefore(today)).count();
        long upcoming = expiring.stream().filter(a -> !a.getWarrantyEndDate().isBefore(today)).count();

        summary.setExpiredWarrantyCount(expired);
        summary.setUpcomingWarrantyExpiryCount(upcoming);

        Map<String, Long> countByType = new HashMap<>();
        for (String type : Arrays.asList("Laptop", "Desktop", "Monitor", "Phone", "Peripheral", "Network Equipment")) {
            countByType.put(type, (long) assetRepository.findByAssetType(type).size());
        }
        summary.setCountByType(countByType);

        return summary;
    }

    @Transactional
    public void deleteAsset(Long id) {
        Asset asset = findAssetOrThrow(id);
        if (assetAssignmentRepository.existsByAssetIdAndReturnedAtIsNull(id)) {
            throw new ConflictException("Cannot delete asset '" + asset.getAssetTag() + "' because it is currently assigned to an employee.");
        }
        // Soft-delete or archive by marking Retired to preserve audit trail
        asset.setStatus("Retired");
        assetRepository.save(asset);
    }

    private void validateStatusTransition(Asset asset, String newStatus) {
        if ("Assigned".equalsIgnoreCase(newStatus) && !"Assigned".equalsIgnoreCase(asset.getStatus())) {
            // Cannot directly switch to Assigned without assignAsset endpoint
            throw new InvalidOperationException("Use the /api/assets/" + asset.getId() + "/assign endpoint to assign an asset with employee and condition details.");
        }
        if ("Assigned".equalsIgnoreCase(asset.getStatus()) && !"Assigned".equalsIgnoreCase(newStatus)) {
            // Cannot change from Assigned to In Stock without returnAsset endpoint
            Optional<AssetAssignment> active = assetAssignmentRepository.findFirstByAssetIdAndReturnedAtIsNull(asset.getId());
            if (active.isPresent()) {
                throw new InvalidOperationException("Asset has an active assignment. Please use the /api/assets/" + asset.getId() + "/return endpoint to return it and set the next status.");
            }
        }
    }

    public Asset findAssetOrThrow(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset with ID " + id + " not found"));
    }

    public AssetResponseDTO mapToResponseDTO(Asset asset) {
        AssetResponseDTO dto = new AssetResponseDTO();
        dto.setId(asset.getId());
        dto.setAssetTag(asset.getAssetTag());
        dto.setAssetType(asset.getAssetType());
        dto.setBrand(asset.getBrand());
        dto.setModel(asset.getModel());
        dto.setSerialNumber(asset.getSerialNumber());
        dto.setStatus(asset.getStatus());
        dto.setPurchaseDate(asset.getPurchaseDate());
        dto.setWarrantyEndDate(asset.getWarrantyEndDate());
        dto.setLocation(asset.getLocation());
        dto.setNotes(asset.getNotes());
        dto.setCreatedAt(asset.getCreatedAt());
        dto.setUpdatedAt(asset.getUpdatedAt());

        // Warranty calculation
        if (asset.getWarrantyEndDate() != null) {
            LocalDate today = LocalDate.now();
            boolean isExpired = asset.getWarrantyEndDate().isBefore(today);
            dto.setWarrantyExpired(isExpired);
            dto.setWarrantyExpiringSoon(!isExpired && asset.getWarrantyEndDate().isBefore(today.plusDays(30)));
            dto.setDaysUntilWarrantyExpiry(ChronoUnit.DAYS.between(today, asset.getWarrantyEndDate()));
        } else {
            dto.setWarrantyExpired(false);
            dto.setWarrantyExpiringSoon(false);
            dto.setDaysUntilWarrantyExpiry(null);
        }

        // Active assignment if exists
        assetAssignmentRepository.findFirstByAssetIdAndReturnedAtIsNull(asset.getId())
                .ifPresent(assignment -> dto.setCurrentAssignment(mapToHistoryDTO(assignment)));

        return dto;
    }

    public AssetAssignmentHistoryDTO mapToHistoryDTO(AssetAssignment aa) {
        AssetAssignmentHistoryDTO dto = new AssetAssignmentHistoryDTO();
        dto.setId(aa.getId());
        dto.setAssetId(aa.getAsset().getId());
        dto.setAssetTag(aa.getAsset().getAssetTag());
        dto.setUserId(aa.getUser().getId());
        dto.setUserName(aa.getUser().getFullName());
        dto.setUserEmail(aa.getUser().getEmail());
        if (aa.getUser().getDepartment() != null) {
            dto.setUserDepartment(aa.getUser().getDepartment().getName());
        }
        dto.setAssignedAt(aa.getAssignedAt());
        dto.setReturnedAt(aa.getReturnedAt());
        dto.setConditionOut(aa.getConditionOut());
        dto.setConditionIn(aa.getConditionIn());
        if (aa.getAssignedBy() != null) {
            dto.setAssignedById(aa.getAssignedBy().getId());
            dto.setAssignedByName(aa.getAssignedBy().getFullName());
        }
        dto.setActive(aa.getReturnedAt() == null);
        return dto;
    }
}

