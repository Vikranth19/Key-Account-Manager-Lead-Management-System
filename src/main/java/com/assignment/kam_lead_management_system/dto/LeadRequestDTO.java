package com.assignment.kam_lead_management_system.dto;

import com.assignment.kam_lead_management_system.domain.Status;
import lombok.Data;

import java.time.Instant;

@Data
public class LeadRequestDTO {
    private String name;
    private String address;
    private Long assignedKamId;
    private Integer callFrequency;
    private Status status;
    private Instant lastCallDate;
}
