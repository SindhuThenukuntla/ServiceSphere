
package com.servicesphere.sla.repository;

import com.servicesphere.sla.entity.SlaPolicy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SlaPolicyRepository
        extends JpaRepository<SlaPolicy, Long> {

    Optional<SlaPolicy> findByPriorityIgnoreCase(String priority);

    List<SlaPolicy> findByEnabledTrue();

    boolean existsByPriorityIgnoreCase(String priority);
}
