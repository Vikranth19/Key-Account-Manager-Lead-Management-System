package com.assignment.kam_lead_management_system.repository;

import com.assignment.kam_lead_management_system.domain.Kam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KamRepository extends JpaRepository<Kam, Long> {
}
