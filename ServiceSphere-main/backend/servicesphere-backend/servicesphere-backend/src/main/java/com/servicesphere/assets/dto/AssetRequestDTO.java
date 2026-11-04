package com.servicesphere.assets.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class AssetRequestDTO {

    @NotBlank(message = "Asset tag is required")
    @Size(min = 2, max = 50, message = "Asset tag must be between 2 and 50 characters")
    private String assetTag;

    @NotBlank(message = "Asset type is required")
    @Pattern(regexp = "^(Laptop|Desktop|Monitor|Phone|Peripheral|Network Equipment)$",
             message = "Asset type must be one of: Laptop, Desktop, Monitor, Phone, Peripheral, Network Equipment")
    private String assetType;

    @NotBlank(message = "Brand is required")
    @Size(max = 100, message = "Brand must not exceed 100 characters")
    private String brand;

    @NotBlank(message = "Model is required")
    @Size(max = 100, message = "Model must not exceed 100 characters")
    private String model;

    @Size(max = 100, message = "Serial number must not exceed 100 characters")
    private String serialNumber;

    @Pattern(regexp = "^(In Stock|Assigned|In Repair|Retired)$",
             message = "Status must be one of: In Stock, Assigned, In Repair, Retired")
    private String status = "In Stock";

    private LocalDate purchaseDate;

    private LocalDate warrantyEndDate;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;

    private String notes;

    public AssetRequestDTO() {}

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
}

