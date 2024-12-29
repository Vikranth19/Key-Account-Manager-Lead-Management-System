package com.assignment.kam_lead_management_system.util;

import com.assignment.kam_lead_management_system.domain.Kam;
import com.assignment.kam_lead_management_system.repository.KamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommonUtils {

    private final KamRepository kamRepository;

    public Long getKamIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return getKamIdFromUsername(username);
    }

    private Long getKamIdFromUsername(String username) {

        Kam kam = kamRepository.findByUsername(username).orElse(null);

        if (kam == null) {
            return null;
        }

        return kam.getId();
    }

}
