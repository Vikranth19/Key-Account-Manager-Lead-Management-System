package com.assignment.kam_lead_management_system.service;

import com.assignment.kam_lead_management_system.domain.Kam;
import com.assignment.kam_lead_management_system.domain.Role;
import com.assignment.kam_lead_management_system.domain.User;
import com.assignment.kam_lead_management_system.dto.UserResponseDTO;
import com.assignment.kam_lead_management_system.dto.UserSignupRequestDTO;
import com.assignment.kam_lead_management_system.exception.KamCustomException;
import com.assignment.kam_lead_management_system.repository.KamRepository;
import com.assignment.kam_lead_management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KamRepository kamRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO signupUser(UserSignupRequestDTO userRequestDTO) {
        // Validate input
        if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
            throw new KamCustomException("Username already exists", HttpStatus.CONFLICT );
        }

        // Create and save user
        User user = User.builder()
                .username(userRequestDTO.getUsername())
                .name(userRequestDTO.getName())
                .password(passwordEncoder.encode(userRequestDTO.getPassword()))
                .email(userRequestDTO.getEmail())
                .role(userRequestDTO.getRole())
                .build();

        user = userRepository.save(user); // Save the user entity

        // If the user is a KAM, create the associated Kam entity
        if (Role.KAM == userRequestDTO.getRole()) {
            Kam kam = Kam.builder()
                    .username(userRequestDTO.getUsername())
                    .name(userRequestDTO.getName())
                    .email(userRequestDTO.getEmail())
                    .phone(userRequestDTO.getPhoneNumber())
                    .user(user)
                    .build();

            kamRepository.save(kam); // Save the Kam entity
        }

        // Build and return response DTO
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .message(user.getRole() + " created successfully")
                .build();
    }

}
