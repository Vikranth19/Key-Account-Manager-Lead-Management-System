package com.assignment.kam_lead_management_system.controller;

import com.assignment.kam_lead_management_system.dto.InteractionRequestDTO;
import com.assignment.kam_lead_management_system.dto.InteractionResponseDTO;
import com.assignment.kam_lead_management_system.service.InteractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/leads/{lead_id}/interactions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class InteractionController {

    private final InteractionService interactionService;

    @PostMapping
    @PreAuthorize("(hasRole('KAM') and @commonUtils.getKamIdFromAuthentication() == @leadService.getKamIdForLead(#lead_id))")
    @Operation(
            summary = "Record Interaction for a Lead",
            description = "This endpoint records an interaction for a specific lead. Only KAM users assigned to the lead can create an interaction.",
            parameters = {
                    @Parameter(name = "lead_id", description = "ID of the lead to record interaction for", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Interaction details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InteractionRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Interaction recorded successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = InteractionResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden: User is not authorized to record interaction for this lead")
            }
    )
    public ResponseEntity<InteractionResponseDTO> recordInteraction(
            @PathVariable Long lead_id,
            @RequestBody InteractionRequestDTO request) {
        InteractionResponseDTO interactionResponseDTO = interactionService.createInteraction(lead_id, request);
        return new ResponseEntity<>(interactionResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('KAM') and @commonUtils.getKamIdFromAuthentication() == @leadService.getKamIdForLead(#lead_id))")
    @Operation(
            summary = "Get Interactions for a Lead",
            description = "This endpoint retrieves all interactions for a specific lead. Both ADMIN and KAM users assigned to the lead can access this data.",
            parameters = {
                    @Parameter(name = "lead_id", description = "ID of the lead to retrieve interactions for", required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Interactions retrieved successfully",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = InteractionResponseDTO.class)))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden: User is not authorized to access interactions for this lead"
                    )
            }
    )
    public ResponseEntity<List<InteractionResponseDTO>> getInteractionsForLead(@PathVariable Long lead_id) {
        List<InteractionResponseDTO> interactions = interactionService.getInteractionsForLead(lead_id);
        return ResponseEntity.ok(interactions);
    }
}
