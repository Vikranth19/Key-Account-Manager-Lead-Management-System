package com.assignment.kam_lead_management_system.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderDetails;
    private Integer quantity;
    private Instant orderDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kam_id", nullable = false)
    private Kam kam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poc_id", nullable = false)
    private Poc poc;
}
