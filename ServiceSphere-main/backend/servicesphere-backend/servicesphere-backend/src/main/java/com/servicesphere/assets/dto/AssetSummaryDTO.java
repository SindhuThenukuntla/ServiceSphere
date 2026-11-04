package com.servicesphere.assets.dto;

import java.util.Map;

public class AssetSummaryDTO {
    private long totalAssets;
    private long inStockCount;
    private long assignedCount;
    private long inRepairCount;
    private long retiredCount;
    private long upcomingWarrantyExpiryCount;
    private long expiredWarrantyCount;
    private Map<String, Long> countByType;

    public AssetSummaryDTO() {}

    public long getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(long totalAssets) {
        this.totalAssets = totalAssets;
    }

    public long getInStockCount() {
        return inStockCount;
    }

    public void setInStockCount(long inStockCount) {
        this.inStockCount = inStockCount;
    }

    public long getAssignedCount() {
        return assignedCount;
    }

    public void setAssignedCount(long assignedCount) {
        this.assignedCount = assignedCount;
    }

    public long getInRepairCount() {
        return inRepairCount;
    }

    public void setInRepairCount(long inRepairCount) {
        this.inRepairCount = inRepairCount;
    }

    public long getRetiredCount() {
        return retiredCount;
    }

    public void setRetiredCount(long retiredCount) {
        this.retiredCount = retiredCount;
    }

    public long getUpcomingWarrantyExpiryCount() {
        return upcomingWarrantyExpiryCount;
    }

    public void setUpcomingWarrantyExpiryCount(long upcomingWarrantyExpiryCount) {
        this.upcomingWarrantyExpiryCount = upcomingWarrantyExpiryCount;
    }

    public long getExpiredWarrantyCount() {
        return expiredWarrantyCount;
    }

    public void setExpiredWarrantyCount(long expiredWarrantyCount) {
        this.expiredWarrantyCount = expiredWarrantyCount;
    }

    public Map<String, Long> getCountByType() {
        return countByType;
    }

    public void setCountByType(Map<String, Long> countByType) {
        this.countByType = countByType;
    }
}

