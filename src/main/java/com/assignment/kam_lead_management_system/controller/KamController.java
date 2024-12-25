package com.assignment.kam_lead_management_system.controller;

import com.assignment.kam_lead_management_system.domain.Kam;
import com.assignment.kam_lead_management_system.dto.KamRequestDTO;
import com.assignment.kam_lead_management_system.dto.KamResponseDTO;
import com.assignment.kam_lead_management_system.service.KamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kams")
@RequiredArgsConstructor
public class KamController {

    private final KamService kamService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createKam(@RequestBody KamRequestDTO kamRequestDTO) {
        Kam createdKam = kamService.createKam(kamRequestDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "KAM created successfully");
        response.put("kam_id", createdKam.getId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public List<KamResponseDTO> getAllKams() {
        return kamService.getAllKams();
    }
}
