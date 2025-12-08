package com.dmh.userservice.controller;

import com.dmh.userservice.dto.request.LogoutRequestDto;
import com.dmh.userservice.dto.request.UserRegisterRequestDto;
import com.dmh.userservice.dto.request.UserUpdateRequestDto;
import com.dmh.userservice.dto.response.UserResponseDto;
import com.dmh.userservice.service.UserService;
import com.dmh.userservice.util.TokenExtractor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@EnableMethodSecurity
@Tag(name = "Users", description = "Endpoints for user registration and profile management")
public class UserController {

    private final UserService userService;
    private final TokenExtractor tokenExtractor;

    public UserController(UserService userService, TokenExtractor tokenExtractor) {
        this.userService = userService;
        this.tokenExtractor = tokenExtractor;
    }

    // ============ REGISTRATION ============

    @Operation(summary = "Register a new user",
            description = "Creates a user in Keycloak, stores profile in DB, and triggers account creation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or email/DNI already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error (Keycloak or Database failure)")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRegisterRequestDto userDto) throws Exception {
        UserResponseDto createdUser = userService.register(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // ============ LOGOUT ============

    @Operation(summary = "Logout user",
            description = "Invalidates the refresh token in Keycloak.")
    @ApiResponse(responseCode = "200", description = "Successfully logged out")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody LogoutRequestDto logoutRequest) {
        userService.logout(logoutRequest);
        return ResponseEntity.ok("Successfully logged out");
    }

    // ============ GET ENDPOINTS ============

    @Operation(summary = "Get current user profile",
            description = "Returns the authenticated user's profile from the JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyProfile(Authentication authentication) {
        String email = tokenExtractor.getEmailFromToken(authentication);
        UserResponseDto user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get user by ID",
            description = "Returns a user's profile. Accessible by the user owner or SERVICE role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found and retrieved"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access this user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SERVICE') or @userSecurity.isOwner(authentication, #id)")
    public ResponseEntity<UserResponseDto> findById(@PathVariable Long id) {
        UserResponseDto user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    // ============ UPDATE ENDPOINTS ============

    @Operation(summary = "Update user profile",
            description = "Updates user details in DB and Keycloak. Only the user owner or SERVICE role can update.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to update this user"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "429", description = "Rate limit - Email can only be changed every 15 days")
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('SERVICE') or @userSecurity.isOwner(authentication, #id)")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequestDto userRequest) throws Exception {
        UserResponseDto updatedUser = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(updatedUser);
    }
}