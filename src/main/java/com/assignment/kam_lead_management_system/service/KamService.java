package com.assignment.kam_lead_management_system.service;

import com.assignment.kam_lead_management_system.domain.Kam;
import com.assignment.kam_lead_management_system.dto.KamResponseDTO;
import com.assignment.kam_lead_management_system.repository.KamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class KamService {

    private final KamRepository kamRepository;

    /**
     * Retrieves all KAMs (Key Account Managers) from the database.
     * This method fetches all the KAM records from the database and converts each
     * record into a KamResponseDTO object for the response.
     *
     * @return List of KamResponseDTO containing details of all KAMs.
     */
    public List<KamResponseDTO> getAllKams() {
        log.info("Fetching all KAMs from the database.");

        List<Kam> kamList = kamRepository.findAll();  // Finds all KAMs

        log.info("Mapped {} KAM(s) to KamResponseDTO.", kamList.size());
        return kamList.stream()
                .map(kam -> KamResponseDTO.builder()
                        .id(kam.getId())
                        .email(kam.getEmail())
                        .username(kam.getUsername())
                        .name(kam.getName())
                        .build())
                .collect(Collectors.toList());
    }
}
