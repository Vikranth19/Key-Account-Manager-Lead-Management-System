package com.assignment.kam_lead_management_system.controller;

import com.assignment.kam_lead_management_system.dto.*;
import com.assignment.kam_lead_management_system.service.LeadService;
import com.assignment.kam_lead_management_system.service.PocService;
import com.assignment.kam_lead_management_system.util.CommonUtils;
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
    public ResponseEntity<LeadResponseDTO> createLead(@RequestBody LeadRequestDTO leadRequestDto) {
        LeadResponseDTO leadResponseDto = leadService.createLead(leadRequestDto);
        return ResponseEntity.ok(leadResponseDto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'KAM')")
    public ResponseEntity<List<LeadResponseDTO>> getAllLeads(
            @RequestParam(required = false) String status) {

        Long kamId = commonUtils.getKamIdFromAuthentication();

        List<LeadResponseDTO> leads = leadService.getAllLeads(kamId, status);
        return ResponseEntity.ok(leads);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateLead(@PathVariable Long id,
                                             @RequestBody LeadRequestDTO leadRequestDto) {
        leadService.updateLead(id, leadRequestDto);
        return ResponseEntity.ok("Lead updated successfully");
    }

    @GetMapping("/requiring-call-today")
    @PreAuthorize("hasAnyRole('ADMIN', 'KAM')")
    public ResponseEntity<List<LeadResponseDTO>> getLeadsRequiringCallToday() {

        Long kamId = commonUtils.getKamIdFromAuthentication();

        List<LeadResponseDTO> leads = leadService.getLeadsRequiringCallToday(kamId);
        return ResponseEntity.ok(leads);
    }

    // POST /leads/{lead_id}/pocs - Add a POC for a specific lead
    @PostMapping("/{lead_id}/pocs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PocResponseDTO> addPocToLead(@PathVariable Long lead_id,
                                                       @RequestBody PocRequestDTO pocRequestDto) {
        PocResponseDTO pocResponseDto = pocService.addPocToLead(lead_id, pocRequestDto);
        return new ResponseEntity<>(pocResponseDto, HttpStatus.CREATED);
    }

    // GET /leads/{lead_id}/pocs - Retrieve all POCs for a specific lead
    @GetMapping("/{lead_id}/pocs")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('KAM') and @commonUtils.getKamIdFromAuthentication() == @leadService.getKamIdForLead(#lead_id))")
    public ResponseEntity<List<PocResponseDTO>> getPocsForLead(@PathVariable Long lead_id) {
        List<PocResponseDTO> pocs = pocService.getPocsForLead(lead_id);
        return new ResponseEntity<>(pocs, HttpStatus.OK);
    }

    @PutMapping("/{lead_id}/reassign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> reassignLeadToKam(
            @PathVariable Long lead_id,
            @RequestParam Long newKamId) {
        leadService.reassignLeadToKam(lead_id, newKamId);
        return ResponseEntity.ok("Reassigned the KAM to the requested lead successfully");
    }

    @GetMapping("/performance")
    @PreAuthorize("hasRole('ADMIN')")
    public List<LeadPerformanceDTO> getLeadPerformance() {
        return leadService.calculateLeadPerformance();
    }

    @GetMapping("/well-performing")
    @PreAuthorize("hasRole('ADMIN')")
    public List<LeadPerformanceDTO> getWellPerformingLeads() {
        return leadService.getLeadsByPerformanceStatus(wellPerformingThreshold, "Well-Performing");
    }

    @GetMapping("/under-performing")
    @PreAuthorize("hasRole('ADMIN')")
    public List<LeadPerformanceDTO> getUnderPerformingLeads() {
        return leadService.getLeadsByPerformanceStatus(underPerformingThreshold, "Under-Performing");
    }
}
