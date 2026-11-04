package com.servicesphere.assets.controller;

import com.servicesphere.assets.dto.*;
import com.servicesphere.assets.service.AssetService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = "*")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping
    public ResponseEntity<Page<AssetResponseDTO>> getAssets(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort) {

        String[] sortParams = sort.split(",");
        String property = sortParams[0];
        Sort.Direction direction = (sortParams.length > 1 && "asc".equalsIgnoreCase(sortParams[1]))
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, property));
        Page<AssetResponseDTO> result = assetService.getAssets(type, status, search, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetResponseDTO> getAssetById(@PathVariable Long id) {
        return ResponseEntity.ok(assetService.getAssetById(id));
    }

    @PostMapping
    public ResponseEntity<AssetResponseDTO> createAsset(@Valid @RequestBody AssetRequestDTO request) {
        AssetResponseDTO created = assetService.createAsset(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AssetResponseDTO> updateAsset(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(assetService.updateAsset(id, updates));
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<AssetResponseDTO> assignAsset(
            @PathVariable Long id,
            @Valid @RequestBody AssetAssignRequestDTO request) {
        return ResponseEntity.ok(assetService.assignAsset(id, request));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<AssetResponseDTO> returnAsset(
            @PathVariable Long id,
            @Valid @RequestBody AssetReturnRequestDTO request) {
        return ResponseEntity.ok(assetService.returnAsset(id, request));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<AssetAssignmentHistoryDTO>> getAssetHistory(@PathVariable Long id) {
        return ResponseEntity.ok(assetService.getAssetAssignmentHistory(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AssetResponseDTO>> getAssignedAssetsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(assetService.getAssignedAssetsForUser(userId));
    }

    @GetMapping("/summary")
    public ResponseEntity<AssetSummaryDTO> getAssetSummary() {
        return ResponseEntity.ok(assetService.getAssetSummary());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }
}

