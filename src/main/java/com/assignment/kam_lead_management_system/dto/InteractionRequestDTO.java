package com.assignment.kam_lead_management_system.dto;

import com.assignment.kam_lead_management_system.domain.InteractionType;
import lombok.Data;


@Data
public class InteractionRequestDTO {

    private InteractionType type;
    private String details;

    // Order-specific fields (Optional, only for type "Order")
    private String orderDetails;
    private Integer quantity;

}
