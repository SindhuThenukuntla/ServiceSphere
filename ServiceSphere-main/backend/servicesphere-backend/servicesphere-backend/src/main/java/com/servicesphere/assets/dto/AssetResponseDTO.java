package com.servicesphere.assets.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AssetResponseDTO {
    private Long id;
    private String assetTag;
    private String assetType;
    private String brand;
    private String model;
    private String serialNumber;
    private String status;
    private LocalDate purchaseDate;
    private LocalDate warrantyEndDate;
    private String location;
    private String notes;
    private boolean warrantyExpired;
    private boolean warrantyExpiringSoon;
    private Long daysUntilWarrantyExpiry;
    private AssetAssignmentHistoryDTO currentAssignment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AssetResponseDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssetTag() {
        return assetTag;
    }

    public void setAssetTag(String assetTag) {
        this.assetTag = assetTag;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getWarrantyEndDate() {
        return warrantyEndDate;
    }

    public void setWarrantyEndDate(LocalDate warrantyEndDate) {
        this.warrantyEndDate = warrantyEndDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isWarrantyExpired() {
        return warrantyExpired;
    }

    public void setWarrantyExpired(boolean warrantyExpired) {
        this.warrantyExpired = warrantyExpired;
    }

    public boolean isWarrantyExpiringSoon() {
        return warrantyExpiringSoon;
    }

    public void setWarrantyExpiringSoon(boolean warrantyExpiringSoon) {
        this.warrantyExpiringSoon = warrantyExpiringSoon;
    }

    public Long getDaysUntilWarrantyExpiry() {
        return daysUntilWarrantyExpiry;
    }

    public void setDaysUntilWarrantyExpiry(Long daysUntilWarrantyExpiry) {
        this.daysUntilWarrantyExpiry = daysUntilWarrantyExpiry;
    }

    public AssetAssignmentHistoryDTO getCurrentAssignment() {
        return currentAssignment;
    }

    public void setCurrentAssignment(AssetAssignmentHistoryDTO currentAssignment) {
        this.currentAssignment = currentAssignment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

