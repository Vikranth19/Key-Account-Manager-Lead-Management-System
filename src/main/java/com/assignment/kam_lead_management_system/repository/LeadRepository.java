package com.assignment.kam_lead_management_system.repository;

import com.assignment.kam_lead_management_system.domain.Lead;
import com.assignment.kam_lead_management_system.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Long> {
    List<Lead> findByKamId(Long kamId);

    List<Lead> findByStatus(Status status);

    List<Lead> findByKamIdAndStatus(Long kamId, Status status);

    @Query("SELECT l FROM Lead l " +
            "WHERE l.callFrequency IS NOT NULL " +
            "AND (l.status = 'NEW' " +
            "OR (l.status = 'CONTACTED' AND DATEDIFF(CURRENT_DATE, l.lastCallDate) >= l.callFrequency))")
    List<Lead> findLeadsRequiringCallToday(LocalDateTime now);
}
