package com.dmh.userservice.mapper;

import com.dmh.userservice.dto.response.AccountResponseDto;
import com.dmh.userservice.dto.request.UserRegisterRequestDto;
import com.dmh.userservice.dto.response.UserResponseDto;
import com.dmh.userservice.entity.User;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class UserDtoMapper {

    public UserRepresentation toUserRepresentation(UserRegisterRequestDto userDto) {
        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(userDto.getEmail());
        kcUser.setEmail(userDto.getEmail());
        kcUser.setFirstName(userDto.getFirstName());
        kcUser.setLastName(userDto.getLastName());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userDto.getPassword());
        credential.setTemporary(false);
        kcUser.setCredentials(Collections.singletonList(credential));

        return kcUser;
    }

    public User toUserEntity(UserRegisterRequestDto userDto, String keycloakId) {
        return User.builder()
                .keycloakId(keycloakId)
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .dni(userDto.getDni())
                .phone(userDto.getPhone())
                .build();
    }

    public UserResponseDto toUserResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .keycloakId(user.getKeycloakId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .dni(user.getDni())
                .phone(user.getPhone())
                .build();
    }

    public UserResponseDto toUserResponseDto(User user, AccountResponseDto account) {
        UserResponseDto dto = toUserResponseDto(user);
        dto.setAccount(account);
        return dto;
    }

}
