package com.assignment.kam_lead_management_system.repository;

import com.assignment.kam_lead_management_system.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByLeadId(Long leadId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.lead.id = :leadId")
    long countOrdersForLead(Long leadId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.lead.id = :leadId AND o.orderDate >= :startDate")
    long countOrdersForLeadInLast30Days(Long leadId, Instant startDate);

}
