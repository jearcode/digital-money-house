package com.dmh.userservice.service.user;

import com.dmh.userservice.dto.response.AccountResponseDto;
import com.dmh.userservice.dto.request.UserRegisterRequestDto;
import com.dmh.userservice.dto.response.UserResponseDto;
import com.dmh.userservice.entity.User;
import com.dmh.userservice.mapper.UserDtoMapper;
import com.dmh.userservice.provider.account.AccountProvider;
import com.dmh.userservice.provider.identity.IdentityProvider;
import com.dmh.userservice.repository.UserRepository;
import com.dmh.userservice.validator.UserRegistrationValidator;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationService {
    private final UserRepository userRepository;
    private final UserRegistrationValidator validator;
    private final IdentityProvider identityProvider;
    private final AccountProvider accountProvider;
    private final UserDtoMapper mapper;

    public UserRegistrationService(
            UserRepository userRepository,
            UserRegistrationValidator validator,
            IdentityProvider identityProvider,
            AccountProvider accountProvider,
            UserDtoMapper mapper) {
        this.userRepository = userRepository;
        this.validator = validator;
        this.identityProvider = identityProvider;
        this.accountProvider = accountProvider;
        this.mapper = mapper;
    }

    public UserResponseDto registerUser(UserRegisterRequestDto userDto){

        // 1. Validate
        validator.validateRegistration(userDto);

        // 2. Create in Keycloak
        UserRepresentation kcUser = mapper.toUserRepresentation(userDto);
        String keycloakId = identityProvider.createUser(kcUser);

        // 3. Save in database
        User newUser = mapper.toUserEntity(userDto, keycloakId);
        User savedUser = userRepository.save(newUser);

        // 4. Create associated account
        AccountResponseDto account = accountProvider.createAccount(savedUser.getId());

        // 5. Return response
        return mapper.toUserResponseDto(savedUser, account);
    }
}
