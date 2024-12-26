package com.assignment.kam_lead_management_system.repository;

import com.assignment.kam_lead_management_system.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByLeadId(Long leadId);
}
