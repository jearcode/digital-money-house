package com.dmh.userservice.dto;

import lombok.Data;

@Data
public class UserRegisterDto {

    private String firstName;
    private String lastName;
    private String dni;
    private String email;
    private String password;
    private String phone;

}
