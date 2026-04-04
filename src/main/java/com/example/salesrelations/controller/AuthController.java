package com.example.salesrelations.controller;

import com.example.salesrelations.dto.LoginRequest;
import com.example.salesrelations.dto.LoginResponse;
import com.example.salesrelations.entity.AllUser;
import com.example.salesrelations.mongo.enums.AuditAction;
import com.example.salesrelations.mongo.enums.AuditStatus;
import com.example.salesrelations.mongo.service.AuditLogService;
import com.example.salesrelations.repository.AllUserRepository;
import com.example.salesrelations.response.ApiResponse;
import com.example.salesrelations.security.JwtService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AllUserRepository allUserRepository;
    private final AuditLogService auditLogService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          AllUserRepository allUserRepository,
                          AuditLogService auditLogService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.allUserRepository = allUserRepository;
        this.auditLogService = auditLogService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            AllUser user = allUserRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole())
                    .disabled(!user.getEnabled())
                    .build();

            String token = jwtService.generateToken(userDetails, user.getRole());

            auditLogService.saveLog(
                    AuditAction.LOGIN,
                    user.getUsername(),
                    "Login",
                    user.getUsername(),
                    AuditStatus.SUCCESS,
                    "Login successful"
            );

            LoginResponse loginResponse = new LoginResponse(
                    token,
                    "Bearer",
                    user.getUsername(),
                    user.getRole()
            );

            ApiResponse<LoginResponse> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Login successful",
                    loginResponse
            );

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            auditLogService.saveLog(
                    AuditAction.LOGIN,
                    request.getUsername(),
                    "Login",
                    request.getUsername(),
                    AuditStatus.FAILED,
                    ex.getMessage()
            );
            throw ex;
        }
    }
}