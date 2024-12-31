package com.assignment.kam_lead_management_system.service;

import com.assignment.kam_lead_management_system.domain.Lead;
import com.assignment.kam_lead_management_system.domain.Poc;
import com.assignment.kam_lead_management_system.dto.PocRequestDTO;
import com.assignment.kam_lead_management_system.dto.PocResponseDTO;
import com.assignment.kam_lead_management_system.exception.KamCustomException;
import com.assignment.kam_lead_management_system.repository.LeadRepository;
import com.assignment.kam_lead_management_system.repository.PocRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class PocService {

    private final PocRepository pocRepository;

    private final LeadRepository leadRepository;

    /**
     * Adds a Point of Contact (POC) to a specific lead.
     *
     * @param leadId The ID of the lead to which the POC will be added.
     * @param pocRequestDto The DTO containing the POC's details.
     * @return PocResponseDTO containing the details of the created POC.
     * @throws KamCustomException if the lead is not found.
     */
    public PocResponseDTO addPocToLead(Long leadId, PocRequestDTO pocRequestDto) {
        log.info("Attempting to add POC to lead with ID: {}", leadId);

        // Validate if lead exists
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new KamCustomException("Lead not found", HttpStatus.NOT_FOUND));

        Poc poc = Poc.builder()
                .name(pocRequestDto.getName())
                .role(pocRequestDto.getRole())
                .contactInfo(pocRequestDto.getContactInfo())
                .email(pocRequestDto.getEmail())
                .lead(lead)
                .build();

        poc = pocRepository.save(poc);

        log.info("POC with ID: {} added successfully to lead with ID: {}", poc.getId(), leadId);

        return PocResponseDTO.builder()
                .id(poc.getId())
                .name(poc.getName())
                .role(poc.getRole())
                .message("POC created successfully")
                .build();
    }

    /**
     * Retrieves all Points of Contact (POC) associated with a specific lead.
     *
     * @param leadId The ID of the lead for which to fetch associated POCs.
     * @return A list of PocResponseDTO containing details of all POCs for the lead.
     * @throws KamCustomException if the lead is not found.
     */
    public List<PocResponseDTO> getPocsForLead(Long leadId) {
        log.info("Fetching all POCs for lead with ID: {}", leadId);

        leadRepository.findById(leadId)
                .orElseThrow(() -> new KamCustomException("Lead not found", HttpStatus.NOT_FOUND));

        List<Poc> pocs = pocRepository.findByLeadId(leadId);

        log.info("Found {} POCs for lead with ID: {}", pocs.size(), leadId);

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
