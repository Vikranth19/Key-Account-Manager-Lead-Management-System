package com.assignment.kam_lead_management_system.repository;

import com.assignment.kam_lead_management_system.domain.Poc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PocRepository extends JpaRepository<Poc, Long> {

    List<Poc> findByLeadId(Long leadId);
}
