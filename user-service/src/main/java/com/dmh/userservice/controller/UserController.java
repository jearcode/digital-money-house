package com.dmh.userservice.controller;

import com.dmh.userservice.dto.LogoutRequestDto;
import com.dmh.userservice.dto.UserRegisterDto;
import com.dmh.userservice.entity.User;
import com.dmh.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Endpoints for user registration and profile management")
public class UserController {

    private final UserService userService;

    public UserController (UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Register a new user", description = "Creates a user in Keycloak, stores profile in DB, and triggers account creation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or email/DNI already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error (Keycloak or Database failure)")
    })
    @PostMapping("/register")
    public ResponseEntity<User> register (@RequestBody UserRegisterDto userDto) {

        User createdUser = userService.register(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        
    }

    @Operation(summary = "Logout user", description = "Invalidates the refresh token in Keycloak.")
    @ApiResponse(responseCode = "200", description = "Successfully logged out")
    @PostMapping("/logout")
    public ResponseEntity<String> logout (@RequestBody LogoutRequestDto logoutRequest) {
        userService.logout(logoutRequest);
        return ResponseEntity.ok("Successfully logged out");
    }

}
