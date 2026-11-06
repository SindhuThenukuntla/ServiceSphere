
package com.servicesphere.sla.controller;

import com.servicesphere.sla.dto.SlaRequest;
import com.servicesphere.sla.entity.SlaPolicy;
import com.servicesphere.sla.service.SlaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sla/policies")
public class SlaController {

    private final SlaService slaService;

    public SlaController(SlaService slaService) {
        this.slaService = slaService;
    }

    // Create SLA policy
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SlaPolicy createPolicy(
            @Valid @RequestBody SlaRequest request) {

        SlaPolicy policy = new SlaPolicy();

        policy.setPriority(request.getPriority());
        policy.setResponseTargetMinutes(
                request.getResponseTargetMinutes());
        policy.setResolutionTargetMinutes(
                request.getResolutionTargetMinutes());
        policy.setWarningThresholdPercent(
                request.getWarningThresholdPercent());
        policy.setEnabled(request.getEnabled());

        return slaService.createPolicy(policy);
    }
    // Get all SLA policies
    @GetMapping
    public List<SlaPolicy> getAllPolicies() {
        return slaService.getAllPolicies();
    }
    // Get enabled policies
    @GetMapping("/active")
    public List<SlaPolicy> getEnabledPolicies() {
        return slaService.getEnabledPolicies();
    }
    // Get policy by priority
    @GetMapping("/{priority}")
    public SlaPolicy getPolicyByPriority(
            @PathVariable String priority) {

        return slaService.getPolicyByPriority(priority);
    }
    // Update SLA policy
    @PutMapping("/{id}")
    public SlaPolicy updatePolicy(
            @PathVariable Long id,
            @Valid @RequestBody SlaRequest request) {

        SlaPolicy policy = new SlaPolicy();

        policy.setResponseTargetMinutes(
                request.getResponseTargetMinutes());
        policy.setResolutionTargetMinutes(
                request.getResolutionTargetMinutes());
        policy.setWarningThresholdPercent(
                request.getWarningThresholdPercent());
        policy.setEnabled(request.getEnabled());

        return slaService.updatePolicy(id, policy);
    }
}
