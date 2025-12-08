package com.dmh.userservice.service.user;

import com.dmh.userservice.dto.request.UserUpdateRequestDto;
import com.dmh.userservice.dto.response.AccountResponseDto;
import com.dmh.userservice.dto.response.UserResponseDto;
import com.dmh.userservice.entity.User;
import com.dmh.userservice.exception.UserNotFoundException;
import com.dmh.userservice.mapper.UserDtoMapper;
import com.dmh.userservice.provider.account.AccountProvider;
import com.dmh.userservice.provider.identity.IdentityProvider;
import com.dmh.userservice.repository.UserRepository;
import com.dmh.userservice.validator.UserUpdateValidator;
import org.springframework.stereotype.Component;


@Component
public class UserUpdateService {

    private final UserRepository userRepository;
    private final UserUpdateValidator validator;
    private final IdentityProvider identityProvider;
    private final AccountProvider accountProvider;
    private final UserDtoMapper mapper;

    public UserUpdateService(
            UserRepository userRepository,
            UserUpdateValidator validator,
            IdentityProvider identityProvider, AccountProvider accountProvider,
            UserDtoMapper mapper) {
        this.userRepository = userRepository;
        this.validator = validator;
        this.identityProvider = identityProvider;
        this.accountProvider = accountProvider;
        this.mapper = mapper;
    }

    public UserResponseDto updateUser(Long userId, UserUpdateRequestDto updateRequest){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        validator.validateUpdate(
                user,
                updateRequest.getEmail(),
                updateRequest.getDni()
        );

        if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(user.getEmail())) {
            identityProvider.updateEmail(user.getKeycloakId(), updateRequest.getEmail());
            user.setEmail(updateRequest.getEmail());
            user.setLastEmailUpdate(java.time.LocalDateTime.now());
        }

        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isBlank()) {
            identityProvider.updatePassword(user.getKeycloakId(), updateRequest.getPassword());
        }

        if (updateRequest.getFirstName() != null) {
            user.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            user.setLastName(updateRequest.getLastName());
        }
        if (updateRequest.getPhone() != null) {
            user.setPhone(updateRequest.getPhone());
        }

        if (updateRequest.getDni() != null && !updateRequest.getDni().equals(user.getDni())) {
            user.setDni(updateRequest.getDni());
        }

        identityProvider.updateProfile(
                user.getKeycloakId(),
                user.getFirstName(),
                user.getLastName()
        );

        User updatedUser = userRepository.save(user);
        AccountResponseDto accountResponseDto = accountProvider.findAccountByUserId(updatedUser.getId());

        return mapper.toUserResponseDto(updatedUser, accountResponseDto);
    }

}
