package com.assignment.kam_lead_management_system.controller;

import com.assignment.kam_lead_management_system.dto.LeadRequestDTO;
import com.assignment.kam_lead_management_system.dto.LeadResponseDTO;
import com.assignment.kam_lead_management_system.dto.PocRequestDTO;
import com.assignment.kam_lead_management_system.dto.PocResponseDTO;
import com.assignment.kam_lead_management_system.service.LeadService;
import com.assignment.kam_lead_management_system.service.PocService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    private final PocService pocService;

    @PostMapping
    public ResponseEntity<LeadResponseDTO> createLead(@RequestBody LeadRequestDTO leadRequestDto) {
        LeadResponseDTO leadResponseDto = leadService.createLead(leadRequestDto);
        return ResponseEntity.ok(leadResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<LeadResponseDTO>> getAllLeads(
            @RequestParam(required = false) Long kamId,
            @RequestParam(required = false) String status) {
        List<LeadResponseDTO> leads = leadService.getAllLeads(kamId, status);
        return ResponseEntity.ok(leads);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateLead(@PathVariable Long id,
                                             @RequestBody LeadRequestDTO leadRequestDto) {
        leadService.updateLead(id, leadRequestDto);
        return ResponseEntity.ok("Lead updated successfully");
    }

    @GetMapping("/requiring-call-today")
    public ResponseEntity<List<LeadResponseDTO>> getLeadsRequiringCallToday() {
        List<LeadResponseDTO> leads = leadService.getLeadsRequiringCallToday();
        return ResponseEntity.ok(leads);
    }

    // POST /leads/{lead_id}/pocs - Add a POC for a specific lead
    @PostMapping("/{lead_id}/pocs")
    public ResponseEntity<PocResponseDTO> addPocToLead(@PathVariable Long lead_id,
                                                       @RequestBody PocRequestDTO pocRequestDto) {
        PocResponseDTO pocResponseDto = pocService.addPocToLead(lead_id, pocRequestDto);
        return new ResponseEntity<>(pocResponseDto, HttpStatus.CREATED);
    }

    // GET /leads/{lead_id}/pocs - Retrieve all POCs for a specific lead
    @GetMapping("/{lead_id}/pocs")
    public ResponseEntity<List<PocResponseDTO>> getPocsForLead(@PathVariable Long lead_id) {
        List<PocResponseDTO> pocs = pocService.getPocsForLead(lead_id);
        return new ResponseEntity<>(pocs, HttpStatus.OK);
    }
}
