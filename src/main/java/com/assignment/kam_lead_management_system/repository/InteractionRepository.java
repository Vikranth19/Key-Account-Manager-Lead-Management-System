package com.assignment.kam_lead_management_system.repository;

import com.assignment.kam_lead_management_system.domain.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InteractionRepository extends JpaRepository<Interaction, Long> {
    List<Interaction> findByLeadId(Long leadId);
}
