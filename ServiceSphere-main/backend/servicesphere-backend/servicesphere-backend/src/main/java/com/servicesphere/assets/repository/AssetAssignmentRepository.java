package com.servicesphere.assets.repository;

import com.servicesphere.assets.entity.AssetAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetAssignmentRepository extends JpaRepository<AssetAssignment, Long> {

    List<AssetAssignment> findByAssetIdOrderByAssignedAtDesc(Long assetId);

    Optional<AssetAssignment> findFirstByAssetIdAndReturnedAtIsNull(Long assetId);

    boolean existsByAssetIdAndReturnedAtIsNull(Long assetId);

    List<AssetAssignment> findByUserIdAndReturnedAtIsNull(Long userId);

    List<AssetAssignment> findByUserIdOrderByAssignedAtDesc(Long userId);

    @Query("SELECT aa FROM AssetAssignment aa WHERE aa.user.id = :userId AND aa.returnedAt IS NULL")
    List<AssetAssignment> findActiveAssignmentsForUser(@Param("userId") Long userId);
}

