package com.assignment.kam_lead_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KamResponseDTO {
    private Long id;
    private String name;
    private String email;
}
