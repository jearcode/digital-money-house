package com.dmh.userservice.service;

import com.dmh.userservice.dto.AccountRequestDto;
import com.dmh.userservice.dto.UserRegisterDto;
import com.dmh.userservice.entity.User;
import com.dmh.userservice.repository.AccountFeignClient;
import com.dmh.userservice.repository.UserRepository;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final Keycloak keycloak;
    private final AccountFeignClient accountFeignClient;

    public UserService (UserRepository userRepository, Keycloak keycloak,
                        AccountFeignClient accountFeignClient) {
        this.userRepository = userRepository;
        this.keycloak = keycloak;
        this.accountFeignClient = accountFeignClient;
    }

    @Value("${dhm.keycloak.realm}")
    private String realm;

    public User register (UserRegisterDto userDto) {

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }
        if (userRepository.existsByDni(userDto.getDni())) {
            throw new RuntimeException("Email address already exists");
        }

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

        UsersResource usersResource = keycloak.realm(realm).users();
        Response response = usersResource.create(kcUser);

        if (response.getStatus() != 201) {
            throw new RuntimeException("Failed to create user in identity provider: " + response.getStatusInfo());
        }

        String keycloakId = CreatedResponseUtil.getCreatedId(response);

        User newUser = User.builder()
                .keycloakId(keycloakId)
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .dni(userDto.getDni())
                .phone(userDto.getPhone())
                .build();

        User savedUser = userRepository.save(newUser);

        String serviceToken = keycloak.tokenManager().getAccessToken().getToken();

        AccountRequestDto accountRequestDto = new AccountRequestDto(savedUser.getId());
        accountFeignClient.createAccount(accountRequestDto, "Bearer " + serviceToken);

        return savedUser;

    }



}
