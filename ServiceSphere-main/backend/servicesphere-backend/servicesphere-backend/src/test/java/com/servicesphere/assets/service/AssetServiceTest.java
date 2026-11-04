package com.servicesphere.assets.service;

import com.servicesphere.assets.dto.AssetAssignRequestDTO;
import com.servicesphere.assets.dto.AssetRequestDTO;
import com.servicesphere.assets.dto.AssetResponseDTO;
import com.servicesphere.assets.dto.AssetReturnRequestDTO;
import com.servicesphere.assets.dto.AssetSummaryDTO;
import com.servicesphere.assets.entity.Asset;
import com.servicesphere.assets.entity.AssetAssignment;
import com.servicesphere.assets.repository.AssetAssignmentRepository;
import com.servicesphere.assets.repository.AssetRepository;
import com.servicesphere.common.exception.ConflictException;
import com.servicesphere.common.exception.InvalidOperationException;
import com.servicesphere.common.exception.ResourceNotFoundException;
import com.servicesphere.users.entity.Role;
import com.servicesphere.users.entity.User;
import com.servicesphere.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private AssetAssignmentRepository assetAssignmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AssetService assetService;

    private Asset sampleAsset;
    private User sampleUser;
    private User sampleAssigner;

    @BeforeEach
    void setUp() {
        sampleAsset = new Asset();
        sampleAsset.setId(1L);
        sampleAsset.setAssetTag("AST-100");
        sampleAsset.setAssetType("Laptop");
        sampleAsset.setBrand("Dell");
        sampleAsset.setModel("XPS 15");
        sampleAsset.setStatus("In Stock");
        sampleAsset.setWarrantyEndDate(LocalDate.now().plusYears(2));

        Role role = new Role("Employee", "Employee role");
        sampleUser = new User("John Doe", "john@servicesphere.local", "hash", role, null);
        sampleUser.setId(10L);

        sampleAssigner = new User("Marcus Vance", "marcus@servicesphere.local", "hash", new Role("IT Asset Manager", "Asset Mgr"), null);
        sampleAssigner.setId(2L);
    }

    @Test
    @DisplayName("Should create asset successfully when tag is unique")
    void testCreateAsset_Success() {
        AssetRequestDTO request = new AssetRequestDTO();
        request.setAssetTag("AST-100");
        request.setAssetType("Laptop");
        request.setBrand("Dell");
        request.setModel("XPS 15");

        when(assetRepository.existsByAssetTag("AST-100")).thenReturn(false);
        when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> {
            Asset a = invocation.getArgument(0);
            a.setId(1L);
            return a;
        });

        AssetResponseDTO response = assetService.createAsset(request);

        assertNotNull(response);
        assertEquals("AST-100", response.getAssetTag());
        assertEquals("In Stock", response.getStatus());
        verify(assetRepository, times(1)).save(any(Asset.class));
    }

    @Test
    @DisplayName("Should reject asset creation with ConflictException when tag already exists")
    void testCreateAsset_DuplicateTag_ThrowsConflict() {
        AssetRequestDTO request = new AssetRequestDTO();
        request.setAssetTag("AST-100");
        request.setAssetType("Laptop");
        request.setBrand("Dell");
        request.setModel("XPS 15");

        when(assetRepository.existsByAssetTag("AST-100")).thenReturn(true);

        assertThrows(ConflictException.class, () -> assetService.createAsset(request));
        verify(assetRepository, never()).save(any(Asset.class));
    }

    @Test
    @DisplayName("Should successfully assign an In Stock asset to an employee and record history")
    void testAssignAsset_Success() {
        AssetAssignRequestDTO request = new AssetAssignRequestDTO(10L, "New device in sealed box", 2L);

        when(assetRepository.findById(1L)).thenReturn(Optional.of(sampleAsset));
        when(assetAssignmentRepository.existsByAssetIdAndReturnedAtIsNull(1L)).thenReturn(false);
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(sampleAssigner));
        when(assetRepository.save(any(Asset.class))).thenAnswer(inv -> inv.getArgument(0));

        AssetResponseDTO response = assetService.assignAsset(1L, request);

        assertEquals("Assigned", response.getStatus());
        verify(assetAssignmentRepository, times(1)).save(any(AssetAssignment.class));
        verify(assetRepository, times(1)).save(sampleAsset);
    }

    @Test
    @DisplayName("Integrity Rule: Should reject assignment if asset already has an active assignment")
    void testAssignAsset_AlreadyAssigned_ThrowsConflict() {
        sampleAsset.setStatus("Assigned");
        AssetAssignRequestDTO request = new AssetAssignRequestDTO(10L, "Good condition", 2L);

        when(assetRepository.findById(1L)).thenReturn(Optional.of(sampleAsset));

        assertThrows(ConflictException.class, () -> assetService.assignAsset(1L, request));
        verify(assetAssignmentRepository, never()).save(any(AssetAssignment.class));
    }

    @Test
    @DisplayName("Integrity Rule: Should reject assignment if asset is Retired")
    void testAssignAsset_Retired_ThrowsInvalidOperation() {
        sampleAsset.setStatus("Retired");
        AssetAssignRequestDTO request = new AssetAssignRequestDTO(10L, "Good condition", 2L);

        when(assetRepository.findById(1L)).thenReturn(Optional.of(sampleAsset));

        InvalidOperationException ex = assertThrows(InvalidOperationException.class,
                () -> assetService.assignAsset(1L, request));
        assertTrue(ex.getMessage().contains("Retired"));
        verify(assetAssignmentRepository, never()).save(any(AssetAssignment.class));
    }

    @Test
    @DisplayName("Integrity Rule: Should reject assignment if asset is In Repair")
    void testAssignAsset_InRepair_ThrowsInvalidOperation() {
        sampleAsset.setStatus("In Repair");
        AssetAssignRequestDTO request = new AssetAssignRequestDTO(10L, "Good condition", 2L);

        when(assetRepository.findById(1L)).thenReturn(Optional.of(sampleAsset));

        InvalidOperationException ex = assertThrows(InvalidOperationException.class,
                () -> assetService.assignAsset(1L, request));
        assertTrue(ex.getMessage().contains("In Repair"));
        verify(assetAssignmentRepository, never()).save(any(AssetAssignment.class));
    }

    @Test
    @DisplayName("Should successfully return an assigned asset and transition status back to In Stock")
    void testReturnAsset_Success() {
        sampleAsset.setStatus("Assigned");
        AssetAssignment activeAssignment = new AssetAssignment(sampleAsset, sampleUser, LocalDateTime.now().minusDays(10), "Good", sampleAssigner);

        when(assetRepository.findById(1L)).thenReturn(Optional.of(sampleAsset));
        when(assetAssignmentRepository.findFirstByAssetIdAndReturnedAtIsNull(1L)).thenReturn(Optional.of(activeAssignment));
        when(assetRepository.save(any(Asset.class))).thenAnswer(inv -> inv.getArgument(0));

        AssetReturnRequestDTO request = new AssetReturnRequestDTO("Returned with no scratches", "In Stock");
        AssetResponseDTO response = assetService.returnAsset(1L, request);

        assertEquals("In Stock", response.getStatus());
        assertNotNull(activeAssignment.getReturnedAt());
        assertEquals("Returned with no scratches", activeAssignment.getConditionIn());
        verify(assetAssignmentRepository, times(1)).save(activeAssignment);
    }

    @Test
    @DisplayName("Should reject return if asset has no active assignment")
    void testReturnAsset_NoActiveAssignment_ThrowsInvalidOperation() {
        sampleAsset.setStatus("In Stock");

        when(assetRepository.findById(1L)).thenReturn(Optional.of(sampleAsset));
        when(assetAssignmentRepository.findFirstByAssetIdAndReturnedAtIsNull(1L)).thenReturn(Optional.empty());

        AssetReturnRequestDTO request = new AssetReturnRequestDTO("Condition", "In Stock");
        assertThrows(InvalidOperationException.class, () -> assetService.returnAsset(1L, request));
    }

    @Test
    @DisplayName("Should return accurate asset summary totals and warranty expiry warnings")
    void testGetAssetSummary() {
        when(assetRepository.count()).thenReturn(10L);
        when(assetRepository.countByStatus("In Stock")).thenReturn(5L);
        when(assetRepository.countByStatus("Assigned")).thenReturn(3L);
        when(assetRepository.countByStatus("In Repair")).thenReturn(1L);
        when(assetRepository.countByStatus("Retired")).thenReturn(1L);

        Asset expiringAsset = new Asset();
        expiringAsset.setWarrantyEndDate(LocalDate.now().plusDays(10));
        when(assetRepository.findAssetsWithUpcomingOrExpiredWarranty(any(LocalDate.class)))
                .thenReturn(List.of(expiringAsset));
        when(assetRepository.findByAssetType(anyString())).thenReturn(Collections.emptyList());

        AssetSummaryDTO summary = assetService.getAssetSummary();

        assertEquals(10L, summary.getTotalAssets());
        assertEquals(5L, summary.getInStockCount());
        assertEquals(3L, summary.getAssignedCount());
        assertEquals(1L, summary.getInRepairCount());
        assertEquals(1L, summary.getRetiredCount());
        assertEquals(1L, summary.getUpcomingWarrantyExpiryCount());
        assertEquals(0L, summary.getExpiredWarrantyCount());
    }
}

