package com.dmh.userservice.provider.identity;

import com.dmh.userservice.dto.request.LogoutRequestDto;
import org.keycloak.representations.idm.UserRepresentation;

public interface IdentityProvider {
    String createUser(UserRepresentation userRepresentation);
    void updateEmail(String keycloakId, String newEmail);
    void updateProfile(String keycloakId, String firstName, String lastName);
    void updatePassword(String keycloakId, String newPassword);
    void logout(LogoutRequestDto logoutRequest);
}
