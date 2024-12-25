package com.assignment.kam_lead_management_system.dto;

import com.assignment.kam_lead_management_system.domain.Role;
import lombok.Data;

@Data
public class PocRequestDTO {

    private String name;
    private Role role;
    private String contactInfo;
    private String email;

}
