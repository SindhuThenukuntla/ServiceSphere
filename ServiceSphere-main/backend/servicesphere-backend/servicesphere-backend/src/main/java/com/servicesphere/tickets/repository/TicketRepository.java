package com.servicesphere.tickets.repository;

import com.servicesphere.tickets.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByTicketNumber(String ticketNumber);

    List<Ticket> findByAssetId(Long assetId);

    List<Ticket> findByRequesterId(Long requesterId);

    List<Ticket> findByAssetIdAndStatusNot(Long assetId, String status);
}

