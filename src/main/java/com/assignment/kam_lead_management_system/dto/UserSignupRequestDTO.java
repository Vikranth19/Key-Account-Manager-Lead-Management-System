package com.assignment.kam_lead_management_system.dto;

import com.assignment.kam_lead_management_system.domain.Role;
import lombok.Data;

@Data
public class UserSignupRequestDTO {
    private String username;
    private String name;
    private String password;
    private String email;
    private Role role; // ADMIN or KAM

    // KAM-specific fields
    private String phoneNumber;

}
