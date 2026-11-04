package com.servicesphere.assets.repository;

import com.servicesphere.assets.entity.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findByAssetTag(String assetTag);

    boolean existsByAssetTag(String assetTag);

    boolean existsByAssetTagAndIdNot(String assetTag, Long id);

    List<Asset> findByStatus(String status);

    List<Asset> findByAssetType(String assetType);

    @Query("SELECT a FROM Asset a WHERE " +
           "(:type IS NULL OR a.assetType = :type) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:search IS NULL OR " +
           "   LOWER(a.assetTag) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "   LOWER(a.brand) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "   LOWER(a.model) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "   LOWER(a.serialNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "   LOWER(a.location) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Asset> findByFilters(@Param("type") String type,
                              @Param("status") String status,
                              @Param("search") String search,
                              Pageable pageable);

    @Query("SELECT a FROM Asset a WHERE a.warrantyEndDate IS NOT NULL AND a.warrantyEndDate <= :thresholdDate")
    List<Asset> findAssetsWithUpcomingOrExpiredWarranty(@Param("thresholdDate") LocalDate thresholdDate);

    long countByStatus(String status);
}

