package com.assignment.kam_lead_management_system.controller;

import com.assignment.kam_lead_management_system.dto.AuthCredentialsRequestDTO;
import com.assignment.kam_lead_management_system.dto.ErrorResponseDTO;
import com.assignment.kam_lead_management_system.dto.UserResponseDTO;
import com.assignment.kam_lead_management_system.dto.UserSignupRequestDTO;
import com.assignment.kam_lead_management_system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    @Operation(
            description = "User sign up - KAM/ADMIN",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User successfully created. The response contains the details of the newly created user.", content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "409", description = "Conflict if the username already exists.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
            })
    public ResponseEntity<UserResponseDTO> signupUser(@RequestBody UserSignupRequestDTO userRequestDTO) {
        UserResponseDTO userResponseDTO = userService.signupUser(userRequestDTO);
        return new ResponseEntity<>(userResponseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(
            description = "User log in to obtain JWT token for accessing protected resources.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully authenticated. The response contains JWT token in the response header."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized. Incorrect username or password.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            })
    public ResponseEntity<String> login(@RequestBody AuthCredentialsRequestDTO request) {
        String token = userService.loginUser(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, token)
                .body("User logged in successfully");
    }
}
