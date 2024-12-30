package com.assignment.kam_lead_management_system.controller;

import com.assignment.kam_lead_management_system.dto.*;
import com.assignment.kam_lead_management_system.service.LeadService;
import com.assignment.kam_lead_management_system.service.PocService;
import com.assignment.kam_lead_management_system.util.CommonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    private final PocService pocService;

    private final CommonUtils commonUtils;

    @Value("${kam.well_performing.threshold}")
    private int wellPerformingThreshold;

    @Value("${kam.under_performing.threshold}")
    private int underPerformingThreshold;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new lead",
            description = "Allows an admin to create a new lead with the provided details.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Lead details to be created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LeadRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Lead created successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LeadResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. Only users with ADMIN role can access this endpoint.",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<LeadResponseDTO> createLead(@RequestBody LeadRequestDTO leadRequestDto) {
        LeadResponseDTO leadResponseDto = leadService.createLead(leadRequestDto);
        return new ResponseEntity<>(leadResponseDto, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'KAM')")
    @Operation(
            summary = "Retrieve all leads",
            description = "Allows an admin to view all leads or a KAM to view only their assigned leads.",
            parameters = {
                    @Parameter(name = "status", description = "Filter leads by status (optional)", required = false)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Leads retrieved successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = LeadResponseDTO.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. Access restricted based on roles.",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<List<LeadResponseDTO>> getAllLeads(
            @RequestParam(required = false) String status) {

        Long kamId = commonUtils.getKamIdFromAuthentication();

        List<LeadResponseDTO> leads = leadService.getAllLeads(kamId, status);
        return ResponseEntity.ok(leads);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update lead details",
            description = "Allows an admin to update the details of a specific lead.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the lead to update", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated lead details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LeadRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lead updated successfully.",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. Access restricted based on roles.",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Lead not found.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
                    )
            }
    )
    public ResponseEntity<String> updateLead(@PathVariable Long id,
                                             @RequestBody LeadRequestDTO leadRequestDto) {
        leadService.updateLead(id, leadRequestDto);
        return ResponseEntity.ok("Lead updated successfully");
    }

    @GetMapping("/requiring-call-today")
    @PreAuthorize("hasAnyRole('ADMIN', 'KAM')")
    @Operation(
            summary = "Get leads requiring call today",
            description = "Retrieve leads requiring follow-up calls based on their call schedule.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Leads retrieved successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = LeadResponseDTO.class))
                            )
                    )
            }
    )
    public ResponseEntity<List<LeadResponseDTO>> getLeadsRequiringCallToday() {

        Long kamId = commonUtils.getKamIdFromAuthentication();

        List<LeadResponseDTO> leads = leadService.getLeadsRequiringCallToday(kamId);
        return ResponseEntity.ok(leads);
    }


    @PostMapping("/{lead_id}/pocs")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Add POC to a lead",
            description = "Allows an admin to add a new POC for a specific lead.",
            parameters = {
                    @Parameter(name = "lead_id", description = "ID of the lead", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "POC details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PocRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "POC added successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PocResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. Access restricted based on roles.",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<PocResponseDTO> addPocToLead(@PathVariable Long lead_id,
                                                       @RequestBody PocRequestDTO pocRequestDto) {
        PocResponseDTO pocResponseDto = pocService.addPocToLead(lead_id, pocRequestDto);
        return new ResponseEntity<>(pocResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{lead_id}/pocs")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('KAM') and @commonUtils.getKamIdFromAuthentication() == @leadService.getKamIdForLead(#lead_id))")
    @Operation(
            summary = "Retrieve all POCs for a lead",
            description = "Allows an admin or the assigned KAM to view POCs associated with a specific lead.",
            parameters = {
                    @Parameter(name = "lead_id", description = "ID of the lead", required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "POCs retrieved successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PocResponseDTO.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. Access restricted based on roles and lead ownership.",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<List<PocResponseDTO>> getPocsForLead(@PathVariable Long lead_id) {
        List<PocResponseDTO> pocs = pocService.getPocsForLead(lead_id);
        return new ResponseEntity<>(pocs, HttpStatus.OK);
    }

    @PutMapping("/{lead_id}/reassign")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Reassign Lead to another KAM",
            description = "Reassigns a lead to a different KAM by providing the lead ID (path variable) and the new KAM ID (query parameter).",
            parameters = {
                    @Parameter(name = "lead_id", description = "ID of the lead to be reassigned", required = true, in = ParameterIn.PATH),
                    @Parameter(name = "newKamId", description = "ID of the new KAM to assign the lead to", required = true, in = ParameterIn.QUERY)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lead reassigned successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. Only users with ADMIN role can access this endpoint."
                    )
            }
    )
    public ResponseEntity<String> reassignLeadToKam(
            @PathVariable Long lead_id,
            @RequestParam Long newKamId) {
        leadService.reassignLeadToKam(lead_id, newKamId);
        return ResponseEntity.ok("Reassigned the KAM to the requested lead successfully");
    }

    @GetMapping("/performance")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get Lead Performance",
            description = "Retrieves a list of lead performance metrics calculated based on the system's criteria.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lead performance details fetched successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LeadPerformanceDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. Only users with ADMIN role can access this endpoint."
                    )
            }
    )
    public List<LeadPerformanceDTO> getLeadPerformance() {
        return leadService.calculateLeadPerformance();
    }

    @GetMapping("/well-performing")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get Well Performing Leads",
            description = "Retrieves a list of well-performing leads based on the predefined performance threshold.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Well-performing leads fetched successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LeadPerformanceDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. Only users with ADMIN role can access this endpoint."
                    )
            }
    )
    public List<LeadPerformanceDTO> getWellPerformingLeads() {
        return leadService.getLeadsByPerformanceStatus(wellPerformingThreshold, "Well-Performing");
    }

    @GetMapping("/under-performing")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get Under Performing Leads",
            description = "Retrieves a list of under-performing leads based on the predefined performance threshold.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Under-performing leads fetched successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LeadPerformanceDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. Only users with ADMIN role can access this endpoint."
                    )
            }
    )
    public List<LeadPerformanceDTO> getUnderPerformingLeads() {
        return leadService.getLeadsByPerformanceStatus(underPerformingThreshold, "Under-Performing");
    }
}
