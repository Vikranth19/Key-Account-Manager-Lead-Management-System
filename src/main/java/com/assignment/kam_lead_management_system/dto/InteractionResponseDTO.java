package com.assignment.kam_lead_management_system.dto;

import com.assignment.kam_lead_management_system.domain.InteractionType;
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
public class InteractionResponseDTO {

    private Long id;
    private InteractionType type;
    private String details;
    private LocalDateTime date;
    private Long leadId;
    private Long kamId;
    private String message;

    // Order-specific response fields (Optional, only for type "Order")
    private Long orderId;
    private String orderDetails;
    private Integer quantity;
}
