package com.assignment.kam_lead_management_system.controller;

import com.assignment.kam_lead_management_system.dto.KamResponseDTO;
import com.assignment.kam_lead_management_system.service.KamService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kams")
@RequiredArgsConstructor
public class KamController {

    private final KamService kamService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<KamResponseDTO> getAllKams() {
        return kamService.getAllKams();
    }
}
