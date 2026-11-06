
package com.servicesphere.sla.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class SlaRequest {

    @NotBlank(message = "Priority is required")
    @Pattern(
            regexp = "(?i)LOW|MEDIUM|HIGH|CRITICAL",
            message = "Priority must be LOW, MEDIUM, HIGH or CRITICAL"
    )
    private String priority;

    @NotNull(message = "Response target is required")
    @Min(value = 1, message = "Response target must be positive")
    private Integer responseTargetMinutes;

    @NotNull(message = "Resolution target is required")
    @Min(value = 1, message = "Resolution target must be positive")
    private Integer resolutionTargetMinutes;

    @NotNull(message = "Warning threshold is required")
    @Min(1)
    @Max(99)
    private Integer warningThresholdPercent = 80;

    private Boolean enabled = true;

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Integer getResponseTargetMinutes() {
        return responseTargetMinutes;
    }

    public void setResponseTargetMinutes(Integer responseTargetMinutes) {
        this.responseTargetMinutes = responseTargetMinutes;
    }

    public Integer getResolutionTargetMinutes() {
        return resolutionTargetMinutes;
    }

    public void setResolutionTargetMinutes(Integer resolutionTargetMinutes) {
        this.resolutionTargetMinutes = resolutionTargetMinutes;
    }

    public Integer getWarningThresholdPercent() {
        return warningThresholdPercent;
    }

    public void setWarningThresholdPercent(Integer warningThresholdPercent) {
        this.warningThresholdPercent = warningThresholdPercent;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
