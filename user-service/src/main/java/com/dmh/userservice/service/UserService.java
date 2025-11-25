package com.dmh.userservice.service;

import com.dmh.userservice.dto.AccountRequestDto;
import com.dmh.userservice.dto.LogoutRequestDto;
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
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final Keycloak keycloak;
    private final AccountFeignClient accountFeignClient;

    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${dmh.keycloak.realm}")
    private String realm;

    @Value("${spring.security.oauth2.client.provider.keycloak.logout-uri}")
    private String logoutUrl;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    public UserService (UserRepository userRepository, Keycloak keycloak,
                        AccountFeignClient accountFeignClient, RestTemplateBuilder restTemplateBuilder) {
        this.userRepository = userRepository;
        this.keycloak = keycloak;
        this.accountFeignClient = accountFeignClient;
        this.restTemplateBuilder = restTemplateBuilder;
    }



    public User register (UserRegisterDto userDto) {

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }
        if (userRepository.existsByDni(userDto.getDni())) {
            throw new RuntimeException("Email address already exists");
        }

        UserRepresentation kcUser = getUserRepresentation(userDto);

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

    private static UserRepresentation getUserRepresentation(UserRegisterDto userDto) {
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

    public void logout (LogoutRequestDto logoutRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("refresh_token", logoutRequest.getRefreshToken());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            restTemplateBuilder.build().postForEntity(logoutUrl, request, Void.class);
        } catch (Exception e) {
            System.out.println("Keycloak logout failed: " + e.getMessage());
        }
    }


}
