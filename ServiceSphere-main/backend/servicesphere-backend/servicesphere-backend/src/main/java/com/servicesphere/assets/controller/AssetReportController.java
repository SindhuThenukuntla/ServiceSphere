package com.servicesphere.assets.controller;

import com.servicesphere.assets.dto.AssetSummaryDTO;
import com.servicesphere.assets.service.AssetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports/assets")
@CrossOrigin(origins = "*")
public class AssetReportController {

    private final AssetService assetService;

    public AssetReportController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping("/summary")
    public ResponseEntity<AssetSummaryDTO> getAssetSummaryReport() {
        return ResponseEntity.ok(assetService.getAssetSummary());
    }
}

