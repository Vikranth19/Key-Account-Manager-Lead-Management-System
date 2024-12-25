package com.assignment.kam_lead_management_system.service;

import com.assignment.kam_lead_management_system.domain.Kam;
import com.assignment.kam_lead_management_system.domain.Lead;
import com.assignment.kam_lead_management_system.domain.Status;
import com.assignment.kam_lead_management_system.dto.LeadRequestDTO;
import com.assignment.kam_lead_management_system.dto.LeadResponseDTO;
import com.assignment.kam_lead_management_system.repository.KamRepository;
import com.assignment.kam_lead_management_system.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final KamRepository kamRepository;

    private final LeadRepository leadRepository;

    @Transactional
    public LeadResponseDTO createLead(LeadRequestDTO leadRequestDto) {
        Kam kam = kamRepository.findById(leadRequestDto.getAssignedKamId())
                .orElseThrow(() -> new RuntimeException("Key Account Manager not found"));

        Lead lead = Lead.builder().
                name(leadRequestDto.getName()).
                location(leadRequestDto.getAddress()).
                status(Status.NEW).
                kam(kam).
                callFrequency(leadRequestDto.getCallFrequency()).
                lastCallDate(null).
                build();

        leadRepository.save(lead);

        return LeadResponseDTO.builder().
                id(lead.getId()).
                message("Lead created successfully").
                build();
    }

    public List<LeadResponseDTO> getAllLeads(Long kamId, String status) {
        List<Lead> leads;

        if (kamId != null && status != null) {
            leads = leadRepository.findByKamIdAndStatus(kamId, Status.valueOf(status));
        } else if (kamId != null) {
            leads = leadRepository.findByKamId(kamId);
        } else if (status != null) {
            leads = leadRepository.findByStatus(Status.valueOf(status));
        } else {
            leads = leadRepository.findAll();
        }

        return leads.stream()
                .map(lead -> LeadResponseDTO.builder()
                        .id(lead.getId())
                        .name(lead.getName())
                        .location(lead.getLocation())
                        .status(lead.getStatus())
                        .assignedKamId(lead.getKam().getId())
                        .callFrequency(lead.getCallFrequency())
                        .lastCallDate(lead.getLastCallDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateLead(Long id, LeadRequestDTO leadRequestDto) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead with requested Id not found"));

        if (leadRequestDto.getStatus() != null) {
            lead.setStatus(leadRequestDto.getStatus());
        }

        if (leadRequestDto.getCallFrequency() != null) {
            lead.setCallFrequency(leadRequestDto.getCallFrequency());
        }

        if (leadRequestDto.getLastCallDate() != null) {
            lead.setLastCallDate(leadRequestDto.getLastCallDate());
        }

        leadRepository.save(lead);
    }

    public List<LeadResponseDTO> getLeadsRequiringCallToday() {
        List<Lead> leads = leadRepository.findLeadsRequiringCallToday(LocalDateTime.now());

        return leads.stream()
                .map(lead -> LeadResponseDTO.builder()
                        .id(lead.getId())
                        .name(lead.getName())
                        .status(lead.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

}