package com.assignment.kam_lead_management_system.controller;

import com.assignment.kam_lead_management_system.dto.InteractionRequestDTO;
import com.assignment.kam_lead_management_system.dto.InteractionResponseDTO;
import com.assignment.kam_lead_management_system.service.OrderService;
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
    public ResponseEntity<InteractionResponseDTO> placeOrder(@PathVariable Long lead_id,
                                                             @RequestBody InteractionRequestDTO interactionRequestDTO) {
        InteractionResponseDTO response = orderService.createOrder(lead_id, interactionRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('KAM') and @commonUtils.getKamIdFromAuthentication() == @leadService.getKamIdForLead(#lead_id))")
    public ResponseEntity<List<InteractionResponseDTO>> getOrders(@PathVariable Long lead_id) {
        List<InteractionResponseDTO> orders = orderService.getOrdersForLead(lead_id);
        return ResponseEntity.ok(orders);
    }
}