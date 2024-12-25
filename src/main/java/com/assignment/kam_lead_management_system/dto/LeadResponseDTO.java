package com.assignment.kam_lead_management_system.dto;

import com.assignment.kam_lead_management_system.domain.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeadResponseDTO {

    private Long id;
    private String name;
    private String location;
    private Status status;
    private Long assignedKamId;
    private Integer callFrequency;
    private String message;
    private LocalDateTime lastCallDate;

}
