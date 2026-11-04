package com.servicesphere.tickets.service;

import com.servicesphere.assets.dto.AssetResponseDTO;
import com.servicesphere.assets.entity.Asset;
import com.servicesphere.assets.repository.AssetAssignmentRepository;
import com.servicesphere.assets.repository.AssetRepository;
import com.servicesphere.assets.service.AssetService;
import com.servicesphere.common.exception.InvalidOperationException;
import com.servicesphere.tickets.dto.TicketAssetLinkDTO;
import com.servicesphere.tickets.dto.TicketResponseDTO;
import com.servicesphere.tickets.entity.Ticket;
import com.servicesphere.tickets.repository.TicketRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketAssetValidationServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private AssetAssignmentRepository assetAssignmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AssetService assetService;

    @InjectMocks
    private TicketAssetValidationService ticketAssetValidationService;

    private Ticket sampleTicket;
    private Asset sampleAsset;

    @BeforeEach
    void setUp() {
        User requester = new User("Sarah Chen", "sarah@servicesphere.local", "hash", new Role("Employee", "Emp"), null);
        requester.setId(5L);

        sampleAsset = new Asset();
        sampleAsset.setId(100L);
        sampleAsset.setAssetTag("AST-002");
        sampleAsset.setBrand("Lenovo");
        sampleAsset.setModel("ThinkPad X1");
        sampleAsset.setStatus("Assigned");

        sampleTicket = new Ticket();
        sampleTicket.setId(1L);
        sampleTicket.setTicketNumber("TICK-1001");
        sampleTicket.setTitle("Screen flicker issue");
        sampleTicket.setDescription("Laptop screen flickers occasionally");
        sampleTicket.setStatus("Open");
        sampleTicket.setRequester(requester);
    }

    @Test
    @DisplayName("Should successfully link an active asset to a ticket")
    void testLinkAssetToTicket_Success() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(sampleTicket));
        when(assetRepository.findById(100L)).thenReturn(Optional.of(sampleAsset));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        AssetResponseDTO assetDto = new AssetResponseDTO();
        assetDto.setId(100L);
        assetDto.setAssetTag("AST-002");
        when(assetService.mapToResponseDTO(sampleAsset)).thenReturn(assetDto);

        TicketResponseDTO response = ticketAssetValidationService.linkAssetToTicket(1L, 100L);

        assertNotNull(response);
        assertEquals("TICK-1001", response.getTicketNumber());
        assertNotNull(response.getLinkedAsset());
        assertEquals("AST-002", response.getLinkedAsset().getAssetTag());
        verify(ticketRepository, times(1)).save(sampleTicket);
    }

    @Test
    @DisplayName("Integrity Rule: Should reject linking a Retired asset to an Open ticket")
    void testLinkAssetToTicket_RetiredAsset_ThrowsInvalidOperation() {
        sampleAsset.setStatus("Retired");
        sampleTicket.setStatus("Open");

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(sampleTicket));
        when(assetRepository.findById(100L)).thenReturn(Optional.of(sampleAsset));

        InvalidOperationException ex = assertThrows(InvalidOperationException.class,
                () -> ticketAssetValidationService.linkAssetToTicket(1L, 100L));
        assertTrue(ex.getMessage().contains("retired"));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Should successfully unlink an asset from a ticket")
    void testUnlinkAssetFromTicket_Success() {
        sampleTicket.setAsset(sampleAsset);

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(sampleTicket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        TicketResponseDTO response = ticketAssetValidationService.unlinkAssetFromTicket(1L);

        assertNotNull(response);
        assertNull(response.getLinkedAsset());
        assertNull(sampleTicket.getAsset());
        verify(ticketRepository, times(1)).save(sampleTicket);
    }

    @Test
    @DisplayName("Should retrieve tickets linked to an asset")
    void testGetTicketsForAsset() {
        sampleTicket.setAsset(sampleAsset);

        when(assetRepository.findById(100L)).thenReturn(Optional.of(sampleAsset));
        when(ticketRepository.findByAssetId(100L)).thenReturn(List.of(sampleTicket));

        List<TicketAssetLinkDTO> links = ticketAssetValidationService.getTicketsForAsset(100L);

        assertNotNull(links);
        assertEquals(1, links.size());
        assertEquals("TICK-1001", links.get(0).getTicketNumber());
        assertEquals("AST-002", links.get(0).getAssetTag());
    }
}

