package com.assignment.kam_lead_management_system.service;

import com.assignment.kam_lead_management_system.domain.Lead;
import com.assignment.kam_lead_management_system.domain.Poc;
import com.assignment.kam_lead_management_system.dto.PocRequestDTO;
import com.assignment.kam_lead_management_system.dto.PocResponseDTO;
import com.assignment.kam_lead_management_system.repository.LeadRepository;
import com.assignment.kam_lead_management_system.repository.PocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PocService {

    private final PocRepository pocRepository;

    private final LeadRepository leadRepository;

    public PocResponseDTO addPocToLead(Long leadId, PocRequestDTO pocRequestDto) {
        // Validate if lead exists
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found"));

        Poc poc = Poc.builder()
                .name(pocRequestDto.getName())
                .role(pocRequestDto.getRole())
                .contactInfo(pocRequestDto.getContactInfo())
                .email(pocRequestDto.getEmail())
                .lead(lead)
                .build();

        poc = pocRepository.save(poc);

        return PocResponseDTO.builder()
                .id(poc.getId())
                .name(poc.getName())
                .role(poc.getRole())
                .message("POC created successfully")
                .build();
    }

    public List<PocResponseDTO> getPocsForLead(Long leadId) {

        leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found"));

        List<Poc> pocs = pocRepository.findByLeadId(leadId);

        return pocs.stream()
                .map(poc -> PocResponseDTO.builder()
                        .id(poc.getId())
                        .name(poc.getName())
                        .role(poc.getRole())
                        .email(poc.getEmail())
                        .build())
                .collect(Collectors.toList());
    }
}
