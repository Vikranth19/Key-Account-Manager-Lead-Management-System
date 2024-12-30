package com.assignment.kam_lead_management_system.controller;

import com.assignment.kam_lead_management_system.dto.InteractionRequestDTO;
import com.assignment.kam_lead_management_system.dto.InteractionResponseDTO;
import com.assignment.kam_lead_management_system.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/leads/{lead_id}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("(hasRole('KAM') and @commonUtils.getKamIdFromAuthentication() == @leadService.getKamIdForLead(#lead_id))")
    @Operation(
            summary = "Create an order for a Lead",
            description = "This endpoint creates an order for a specific lead. Only KAM users assigned to the lead can create an order.",
            parameters = {
                    @Parameter(name = "lead_id", description = "ID of the lead to create order for", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Order details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InteractionRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Order created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = InteractionResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden: User is not authorized to create order for this lead")
            }
    )
    public ResponseEntity<InteractionResponseDTO> placeOrder(@PathVariable Long lead_id,
                                                             @RequestBody InteractionRequestDTO interactionRequestDTO) {
        InteractionResponseDTO response = orderService.createOrder(lead_id, interactionRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('KAM') and @commonUtils.getKamIdFromAuthentication() == @leadService.getKamIdForLead(#lead_id))")
    @Operation(
            summary = "Get orders for a Lead",
            description = "This endpoint retrieves all orders for a specific lead. Both ADMIN and KAM users assigned to the lead can access this data.",
            parameters = {
                    @Parameter(name = "lead_id", description = "ID of the lead to retrieve orders for", required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Orders retrieved successfully",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = InteractionResponseDTO.class)))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden: User is not authorized to access orders for this lead"
                    )
            }
    )
    public ResponseEntity<List<InteractionResponseDTO>> getOrders(@PathVariable Long lead_id) {
        List<InteractionResponseDTO> orders = orderService.getOrdersForLead(lead_id);
        return ResponseEntity.ok(orders);
    }
}