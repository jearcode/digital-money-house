package com.dmh.authservice.controller;

import com.dmh.authservice.dto.LoginRequestDto;
import com.dmh.authservice.dto.TokenResponseDto;
import com.dmh.authservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController (AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login (@RequestBody LoginRequestDto loginRequest) {
        TokenResponseDto token = authService.login(loginRequest);
        return ResponseEntity.ok(token);
    }

}
