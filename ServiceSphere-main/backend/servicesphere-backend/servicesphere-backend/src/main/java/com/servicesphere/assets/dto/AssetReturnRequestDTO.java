package com.servicesphere.assets.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

public class AssetReturnRequestDTO {

    @NotBlank(message = "Condition in is required")
    private String conditionIn;

    private LocalDateTime returnedAt;

    @Pattern(regexp = "^(In Stock|In Repair|Retired)$",
             message = "Next status must be one of: In Stock, In Repair, Retired")
    private String nextStatus = "In Stock";

    public AssetReturnRequestDTO() {}

    public AssetReturnRequestDTO(String conditionIn, String nextStatus) {
        this.conditionIn = conditionIn;
        this.nextStatus = nextStatus != null ? nextStatus : "In Stock";
    }

    public String getConditionIn() {
        return conditionIn;
    }

    public void setConditionIn(String conditionIn) {
        this.conditionIn = conditionIn;
    }

    public LocalDateTime getReturnedAt() {
        return returnedAt;
    }

    public void setReturnedAt(LocalDateTime returnedAt) {
        this.returnedAt = returnedAt;
    }

    public String getNextStatus() {
        return nextStatus;
    }

    public void setNextStatus(String nextStatus) {
        this.nextStatus = nextStatus;
    }
}

