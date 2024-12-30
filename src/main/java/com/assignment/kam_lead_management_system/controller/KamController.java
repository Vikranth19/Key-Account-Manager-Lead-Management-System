package com.assignment.kam_lead_management_system.controller;

import com.assignment.kam_lead_management_system.dto.KamResponseDTO;
import com.assignment.kam_lead_management_system.service.KamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kams")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class KamController {

    private final KamService kamService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Retrieve all Key Account Managers (KAMs)",
            description = "This endpoint allows an admin to retrieve the details of all Key Account Managers (KAMs). Access is restricted to users with the ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of KAMs retrieved successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = KamResponseDTO.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. Only users with ADMIN role can access this endpoint.",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public List<KamResponseDTO> getAllKams() {
        return kamService.getAllKams();
    }
}
