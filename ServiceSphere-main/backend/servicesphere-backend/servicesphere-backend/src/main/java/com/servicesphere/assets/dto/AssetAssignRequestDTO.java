package com.servicesphere.assets.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class AssetAssignRequestDTO {

    @NotNull(message = "Employee user ID is required")
    private Long userId;

    private Long assignedBy;

    private LocalDateTime assignedAt;

    @NotBlank(message = "Condition out is required")
    private String conditionOut;

    public AssetAssignRequestDTO() {}

    public AssetAssignRequestDTO(Long userId, String conditionOut, Long assignedBy) {
        this.userId = userId;
        this.conditionOut = conditionOut;
        this.assignedBy = assignedBy;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(Long assignedBy) {
        this.assignedBy = assignedBy;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public String getConditionOut() {
        return conditionOut;
    }

    public void setConditionOut(String conditionOut) {
        this.conditionOut = conditionOut;
    }
}

