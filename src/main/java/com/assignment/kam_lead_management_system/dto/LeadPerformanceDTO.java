package com.assignment.kam_lead_management_system.dto;

import com.assignment.kam_lead_management_system.domain.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeadPerformanceDTO {

    private Long leadId;
    private String leadName;
    private Status leadStatus;
    private Long totalOrders;
    private Long orderFrequency;
    private String performanceStatus;

}
