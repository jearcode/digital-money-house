package com.dmh.userservice.provider.identity;

import com.dmh.userservice.dto.request.LogoutRequestDto;
import com.dmh.userservice.exception.IdentityProviderException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
public class KeycloakIdentityProvider implements IdentityProvider {

    private final Keycloak keycloak;
    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${dmh.keycloak.realm}")
    private String realm;

    @Value("${spring.security.oauth2.client.provider.keycloak.logout-uri}")
    private String logoutUrl;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;


    public KeycloakIdentityProvider(Keycloak keycloak, RestTemplateBuilder restTemplateBuilder) {
        this.keycloak = keycloak;
        this.restTemplateBuilder = restTemplateBuilder;
    }

    @Override
    public String createUser(UserRepresentation userRepresentation) {
        try {
            UsersResource usersResource = keycloak.realm(realm).users();
            Response response = usersResource.create(userRepresentation);

            if (response.getStatus() != 201) {
                throw new IdentityProviderException(
                        "Keycloak",
                        "create user",
                        response.getStatusInfo().toString()
                );
            }

            return CreatedResponseUtil.getCreatedId(response);
        } catch (Exception e) {
            throw new IdentityProviderException("Keycloak", "create user", e);
        }
    }

    @Override
    public void updateEmail(String keycloakId, String newEmail) {
        try {
            UserResource userResource = keycloak.realm(realm).users().get(keycloakId);
            UserRepresentation kcUser = userResource.toRepresentation();
            kcUser.setEmail(newEmail);
            kcUser.setEmailVerified(false);
            userResource.update(kcUser);
        } catch (Exception e) {
            throw new IdentityProviderException("Keycloak", "update email", e);
        }
    }

    @Override
    public void updateProfile(String keycloakId, String firstName, String lastName) {
        try {
            UserResource userResource = keycloak.realm(realm).users().get(keycloakId);
            UserRepresentation kcUser = userResource.toRepresentation();

            if (firstName != null) kcUser.setFirstName(firstName);
            if (lastName != null) kcUser.setLastName(lastName);

            userResource.update(kcUser);
        } catch (Exception e) {
            throw new IdentityProviderException("Keycloak", "update profile", e);
        }
    }

    @Override
    public void updatePassword(String keycloakId, String newPassword) {
        try {
            UserResource userResource = keycloak.realm(realm).users().get(keycloakId);

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(false);

            userResource.resetPassword(credential);
        } catch (Exception e) {
            throw new IdentityProviderException("Keycloak", "update password", e);
        }
    }

    @Override
    public void logout(LogoutRequestDto logoutRequest) {
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
