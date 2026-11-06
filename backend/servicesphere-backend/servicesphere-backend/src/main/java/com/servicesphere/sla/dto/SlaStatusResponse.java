package com.servicesphere.sla.dto;

public class SlaStatusResponse {

    private String status;
    private Long remainingMinutes;
    private Double elapsedPercentage;
    private String message;

    public SlaStatusResponse() {
    }

    public SlaStatusResponse(
            String status,
            Long remainingMinutes,
            Double elapsedPercentage,
            String message) {

        this.status = status;
        this.remainingMinutes = remainingMinutes;
        this.elapsedPercentage = elapsedPercentage;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getRemainingMinutes() {
        return remainingMinutes;
    }

    public void setRemainingMinutes(Long remainingMinutes) {
        this.remainingMinutes = remainingMinutes;
    }

    public Double getElapsedPercentage() {
        return elapsedPercentage;
    }

    public void setElapsedPercentage(Double elapsedPercentage) {
        this.elapsedPercentage = elapsedPercentage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
