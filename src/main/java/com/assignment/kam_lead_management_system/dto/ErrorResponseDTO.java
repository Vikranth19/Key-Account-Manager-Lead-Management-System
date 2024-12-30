package com.assignment.kam_lead_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ErrorResponseDTO {

    private int status;
    private String message;
    private Instant timestamp;
    private String details;

}
