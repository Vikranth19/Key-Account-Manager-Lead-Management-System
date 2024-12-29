package com.assignment.kam_lead_management_system.controller;

import com.assignment.kam_lead_management_system.dto.InteractionRequestDTO;
import com.assignment.kam_lead_management_system.dto.InteractionResponseDTO;
import com.assignment.kam_lead_management_system.service.InteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/leads/{lead_id}/interactions")
@RequiredArgsConstructor
public class InteractionController {

    private final InteractionService interactionService;

    @PostMapping
    @PreAuthorize("(hasRole('KAM') and @commonUtils.getKamIdFromAuthentication() == @leadService.getKamIdForLead(#lead_id))")
    public ResponseEntity<InteractionResponseDTO> recordInteraction(
            @PathVariable Long lead_id,
            @RequestBody InteractionRequestDTO request) {
        InteractionResponseDTO response = interactionService.createInteraction(lead_id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('KAM') and @commonUtils.getKamIdFromAuthentication() == @leadService.getKamIdForLead(#lead_id))")
    public ResponseEntity<List<InteractionResponseDTO>> getInteractionsForLead(@PathVariable Long lead_id) {
        List<InteractionResponseDTO> interactions = interactionService.getInteractionsForLead(lead_id);
        return ResponseEntity.ok(interactions);
    }
}
