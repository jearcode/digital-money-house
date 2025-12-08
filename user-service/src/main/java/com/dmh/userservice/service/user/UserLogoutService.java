package com.dmh.userservice.service.user;

import com.dmh.userservice.dto.request.LogoutRequestDto;
import com.dmh.userservice.provider.identity.IdentityProvider;
import org.springframework.stereotype.Component;

@Component
public class UserLogoutService {

    private final IdentityProvider identityProvider;

    public UserLogoutService(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    public void logout(LogoutRequestDto logoutRequest) {
        identityProvider.logout(logoutRequest);
    }

}
