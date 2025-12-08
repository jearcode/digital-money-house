package com.dmh.userservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {

    private Long id;
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String email;
    private String dni;
    private String phone;
    private AccountResponseDto account;

}
