package com.dmh.accountservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

    private Long id;
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String email;
    private String dni;
    private String phone;
    private AccountDto account;

}
