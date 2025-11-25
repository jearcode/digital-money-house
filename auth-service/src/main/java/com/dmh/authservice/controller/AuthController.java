package com.dmh.authservice.controller;

import com.dmh.authservice.dto.LoginRequestDto;
import com.dmh.authservice.dto.TokenResponseDto;
import com.dmh.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")@Tag(name = "Authentication", description = "Endpoints for login and session management")
public class AuthController {

    private final AuthService authService;

    public AuthController (AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "User Login", description = "Authenticates a user via Keycloak and returns JWT tokens.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login (@Valid @RequestBody LoginRequestDto loginRequest){
        TokenResponseDto token = authService.login(loginRequest);
        return ResponseEntity.ok(token);
    }

}
