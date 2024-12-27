package com.assignment.kam_lead_management_system.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="restaurant_lead")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.NEW;

    @ManyToOne
    @JoinColumn(name = "kam_id", nullable = false)
    private Kam kam;

    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Poc> pocs = new ArrayList<>();

    private Integer callFrequency;
    private LocalDateTime lastCallDate;
}

