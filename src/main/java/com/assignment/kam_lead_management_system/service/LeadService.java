package com.assignment.kam_lead_management_system.service;

import com.assignment.kam_lead_management_system.domain.Kam;
import com.assignment.kam_lead_management_system.domain.Lead;
import com.assignment.kam_lead_management_system.domain.Status;
import com.assignment.kam_lead_management_system.dto.LeadPerformanceDTO;
import com.assignment.kam_lead_management_system.dto.LeadRequestDTO;
import com.assignment.kam_lead_management_system.dto.LeadResponseDTO;
import com.assignment.kam_lead_management_system.exception.KamCustomException;
import com.assignment.kam_lead_management_system.repository.KamRepository;
import com.assignment.kam_lead_management_system.repository.LeadRepository;
import com.assignment.kam_lead_management_system.repository.OrderRepository;
import com.assignment.kam_lead_management_system.strategy.PerformanceStrategy;
import com.assignment.kam_lead_management_system.strategy.UnderPerformingStrategy;
import com.assignment.kam_lead_management_system.strategy.WellPerformingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class LeadService {

    private final KamRepository kamRepository;

    private final LeadRepository leadRepository;

    private final OrderRepository orderRepository;

    /**
     * Creates a new lead and assigns it to a KAM (Key Account Manager).
     *
     * @param leadRequestDto The DTO containing the lead details.
     * @return LeadResponseDTO containing the details of the created lead.
     * @throws KamCustomException if the KAM is not found.
     */
    @Transactional
    public LeadResponseDTO createLead(LeadRequestDTO leadRequestDto) {
        log.info("Creating a new lead for KAM with ID: {}", leadRequestDto.getAssignedKamId());

        Kam kam = kamRepository.findById(leadRequestDto.getAssignedKamId())
                .orElseThrow(() -> new KamCustomException("Key Account Manager not found", HttpStatus.NOT_FOUND));

        Lead lead = Lead.builder().
                name(leadRequestDto.getName()).
                location(leadRequestDto.getAddress()).
                status(Status.NEW).
                kam(kam).
                callFrequency(leadRequestDto.getCallFrequency()).
                lastCallDate(null).
                build();

        leadRepository.save(lead);

        log.info("Lead with ID: {} created successfully.", lead.getId());
        return LeadResponseDTO.builder().
                id(lead.getId()).
                message("Lead created successfully").
                build();
    }

    /**
     * Retrieves all leads based on KAM ID and/or status.
     *
     * @param kamId The ID of the KAM (optional).
     * @param status The status of the leads (optional).
     * @return A list of LeadResponseDTO containing lead details.
     */
    public List<LeadResponseDTO> getAllLeads(Long kamId, String status) {
        log.info("Fetching all leads with kamId: {} and status: {}", kamId, status);

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

        log.info("Fetched {} lead(s).", leads.size());

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

    /**
     * Updates the details of an existing lead.
     *
     * @param id The ID of the lead to be updated.
     * @param leadRequestDto The DTO containing the updated lead details.
     * @throws KamCustomException if the lead is not found.
     */
    @Transactional
    public void updateLead(Long id, LeadRequestDTO leadRequestDto) {
        log.info("Updating lead with ID: {}", id);

        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new KamCustomException("Lead with requested Id not found", HttpStatus.NOT_FOUND));

        if (leadRequestDto.getStatus() != null) {
            lead.setStatus(leadRequestDto.getStatus());
        }

        if (leadRequestDto.getCallFrequency() != null) {
            lead.setCallFrequency(leadRequestDto.getCallFrequency());
        }

        if (leadRequestDto.getLastCallDate() != null) {
            lead.setLastCallDate(leadRequestDto.getLastCallDate());
        }

        if(leadRequestDto.getAddress() != null){
            lead.setLocation(leadRequestDto.getAddress());
        }

        leadRepository.save(lead);

        log.info("Lead with ID: {} updated successfully.", id);
    }

    /**
     * Retrieves leads that require a call today.
     *
     * @param kamId The ID of the KAM.
     * @return A list of LeadResponseDTO for leads requiring a call today.
     */
    public List<LeadResponseDTO> getLeadsRequiringCallToday(Long kamId) {
        log.info("Fetching leads for KAM with ID: {} that require a call today.", kamId);

        List<Lead> leads = leadRepository.findLeadsRequiringCallToday(Instant.now().truncatedTo(ChronoUnit.SECONDS), kamId);

        log.info("Found {} lead(s) requiring a call today.", leads.size());

        return leads.stream()
                .map(lead -> LeadResponseDTO.builder()
                        .id(lead.getId())
                        .name(lead.getName())
                        .status(lead.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Reassigns a lead to a new KAM.
     *
     * @param leadId The ID of the lead to be reassigned.
     * @param newKamId The ID of the new KAM.
     * @throws KamCustomException if the lead or the new KAM is not found.
     */
    @Transactional
    public void reassignLeadToKam(Long leadId, Long newKamId) {
        log.info("Reassigning lead with ID: {} to new KAM with ID: {}", leadId, newKamId);

        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new KamCustomException("Lead not found", HttpStatus.NOT_FOUND));
        Kam newKam = kamRepository.findById(newKamId)
                .orElseThrow(() -> new KamCustomException("KAM not found", HttpStatus.NOT_FOUND));

        lead.setKam(newKam);
        leadRepository.save(lead);

        log.info("Lead with ID: {} successfully reassigned to KAM with ID: {}.", leadId, newKamId);
    }

    /**
     * Calculates the performance of all leads based on the number of orders placed.
     *
     * @return A list of LeadPerformanceDTO containing performance details of all leads.
     */
    public List<LeadPerformanceDTO> calculateLeadPerformance() {
        log.info("Calculating lead performance based on order count.");

        // Fetch leads from the database
        List<LeadPerformanceDTO> performanceDTOs = new ArrayList<>();

        List<Lead> leads = leadRepository.findAll();

        for (Lead lead : leads) {
            long orderCount = orderRepository.countOrdersForLead(lead.getId());

            LeadPerformanceDTO performanceDTO = LeadPerformanceDTO.builder()
                    .leadId(lead.getId())
                    .leadName(lead.getName())
                    .leadStatus(lead.getStatus())
                    .totalOrders(orderCount)
                    .orderFrequency(orderCount)
                    .build();

            performanceDTOs.add(performanceDTO);
        }

        log.info("Calculated performance for {} lead(s).", performanceDTOs.size());

        return performanceDTOs;
    }

    /**
     * Retrieves leads based on their performance status.
     *
     * @param orderThreshold The threshold for performance evaluation.
     * @param performanceStatus The performance status to filter leads by.
     * @return A list of LeadPerformanceDTO containing performance details of leads.
     */
    public List<LeadPerformanceDTO> getLeadsByPerformanceStatus(int orderThreshold, String performanceStatus) {
        log.info("Fetching leads with performance status: {}", performanceStatus);

        List<LeadPerformanceDTO> performanceDTOs = new ArrayList<>();
        List<Lead> leads = leadRepository.findAll();
        Instant startDate = Instant.now().minus(30, ChronoUnit.DAYS);

        PerformanceStrategy strategy;

        // Decide which strategy to use based on the performanceStatus
        if ("Well-Performing".equals(performanceStatus)) {
            strategy = new WellPerformingStrategy();
        } else if ("Under-Performing".equals(performanceStatus)) {
            strategy = new UnderPerformingStrategy();
        } else {
            throw new IllegalArgumentException("Invalid performance status: " + performanceStatus);
        }

        for (Lead lead : leads) {
            long orderCount = orderRepository.countOrdersForLeadInLast30Days(lead.getId(), startDate);
            LeadPerformanceDTO performanceDTO = buildLeadPerformanceDTO(lead, orderCount);

            if (strategy.evaluate(orderCount, orderThreshold)) {
                performanceDTO.setPerformanceStatus(strategy.getPerformanceStatus());
                performanceDTOs.add(performanceDTO);
            }
        }

        log.info("Fetched {} lead(s) with performance status: {}", performanceDTOs.size(), performanceStatus);

        return performanceDTOs;
    }


    private LeadPerformanceDTO buildLeadPerformanceDTO(Lead lead, long orderCount) {
        return LeadPerformanceDTO.builder()
                .leadId(lead.getId())
                .leadName(lead.getName())
                .leadStatus(lead.getStatus())
                .totalOrders(orderCount)
                .orderFrequency(orderCount)
                .build();
    }

    /**
     * Retrieves the KAM ID associated with a lead.
     *
     * @param leadId The ID of the lead.
     * @return The ID of the associated KAM.
     * @throws KamCustomException if the lead is not found.
     */
    public Long getKamIdForLead(Long leadId) {
        log.info("Fetching KAM ID for lead with ID: {}", leadId);

        Lead lead = leadRepository.findById(leadId).orElseThrow(() -> new KamCustomException("Lead not found", HttpStatus.NOT_FOUND));
        Long kamId = lead.getKam() != null ? lead.getKam().getId() : null;

        log.info("KAM ID for lead with ID: {} is {}", leadId, kamId);
        return kamId;
    }


}
