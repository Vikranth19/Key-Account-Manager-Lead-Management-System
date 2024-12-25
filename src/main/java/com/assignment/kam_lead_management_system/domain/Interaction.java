package com.assignment.kam_lead_management_system.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name="interaction")
@Data
public class Interaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String details;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;
}

