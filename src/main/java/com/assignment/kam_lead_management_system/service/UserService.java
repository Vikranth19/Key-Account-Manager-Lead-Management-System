package com.assignment.kam_lead_management_system.service;

import com.assignment.kam_lead_management_system.domain.Kam;
import com.assignment.kam_lead_management_system.domain.Role;
import com.assignment.kam_lead_management_system.domain.User;
import com.assignment.kam_lead_management_system.dto.AuthCredentialsRequestDTO;
import com.assignment.kam_lead_management_system.dto.UserResponseDTO;
import com.assignment.kam_lead_management_system.dto.UserSignupRequestDTO;
import com.assignment.kam_lead_management_system.exception.KamCustomException;
import com.assignment.kam_lead_management_system.repository.KamRepository;
import com.assignment.kam_lead_management_system.repository.UserRepository;
import com.assignment.kam_lead_management_system.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

    private final UserRepository userRepository;
    private final KamRepository kamRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * Sign up a new user in the system. If the user is of role KAM, a corresponding KAM entity is also created.
     *
     * @param userRequestDTO DTO containing the details for user creation.
     * @return UserResponseDTO containing user information and a success message.
     * @throws KamCustomException if the username already exists.
     */
    @Transactional
    public UserResponseDTO signupUser(UserSignupRequestDTO userRequestDTO) {
        log.info("Starting user signup for username: {}", userRequestDTO.getUsername());

        // Validate input
        if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
            log.warn("Attempt to sign up with an already existing username: {}", userRequestDTO.getUsername());
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

        user = userRepository.save(user);
        log.info("User created successfully: {}", user.getUsername());

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
            log.info("KAM entity created for user: {}", user.getUsername());
        }

        // Build and return response DTO
        log.info("Returning response DTO for user: {}", user.getUsername());
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .message(user.getRole() + " created successfully")
                .build();
    }

    /**
     * Logs in a user by authenticating their credentials and generating a JWT token.
     *
     * @param authCredentialsRequestDTO DTO containing the username and password for authentication.
     * @return JWT token if credentials are valid.
     * @throws KamCustomException if the credentials are invalid.
     */
    public String loginUser(AuthCredentialsRequestDTO authCredentialsRequestDTO){
        log.info("Attempting to log in for username: {}", authCredentialsRequestDTO.getUsername());

        try {
            Authentication authenticate = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    authCredentialsRequestDTO.getUsername(), authCredentialsRequestDTO.getPassword()));

            User user = (User) authenticate.getPrincipal();
            user.setPassword(null);

            log.info("Login successful for username: {}", user.getUsername());
            return jwtUtil.generateToken(user);
        } catch (BadCredentialsException ex) {
            throw new KamCustomException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }

}
