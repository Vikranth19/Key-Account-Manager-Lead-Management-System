package com.assignment.kam_lead_management_system.dto;

import com.assignment.kam_lead_management_system.domain.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PocResponseDTO {

    private Long id;
    private String name;
    private Role role;
    private String email;
    private String message;
}
