package com.dmh.userservice.configuration.security;


import com.dmh.userservice.dto.response.UserResponseDto;
import com.dmh.userservice.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    private UserService userService;

    public UserSecurity (UserService userService) {
        this.userService = userService;
    }

    public boolean isOwner (Authentication authentication, Long userId) {
        try {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String emailInToken = jwt.getClaim("email");

            UserResponseDto user = userService.findByEmail(emailInToken);

            return userId.equals(user.getId());

        } catch (Exception e) {
            return false;
        }
    }

}
