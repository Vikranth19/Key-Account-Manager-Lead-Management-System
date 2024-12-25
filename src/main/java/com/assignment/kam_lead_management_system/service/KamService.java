package com.assignment.kam_lead_management_system.service;

import com.assignment.kam_lead_management_system.domain.Kam;
import com.assignment.kam_lead_management_system.dto.KamRequestDTO;
import com.assignment.kam_lead_management_system.dto.KamResponseDTO;
import com.assignment.kam_lead_management_system.repository.KamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KamService {

    private final KamRepository kamRepository;

    public Kam createKam(KamRequestDTO kamRequestDTO) {
        Kam kam = Kam.builder()
                .name(kamRequestDTO.getName())
                .email(kamRequestDTO.getEmail())
                .phone(kamRequestDTO.getPhone())
                .build();
        return kamRepository.save(kam);  // Saves the KAM to the database
    }

    public List<KamResponseDTO> getAllKams() {
        List<Kam> kamList = kamRepository.findAll();  // Finds all KAMs
        return kamList.stream()
                .map(kam -> KamResponseDTO.builder()
                        .id(kam.getId())
                        .email(kam.getEmail())
                        .name(kam.getName())
                        .build())
                .collect(Collectors.toList());
    }
}
