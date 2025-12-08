package com.dmh.userservice.service;

import com.dmh.userservice.dto.request.LogoutRequestDto;
import com.dmh.userservice.dto.request.UserRegisterRequestDto;
import com.dmh.userservice.dto.request.UserUpdateRequestDto;
import com.dmh.userservice.dto.response.AccountResponseDto;
import com.dmh.userservice.dto.response.UserResponseDto;
import com.dmh.userservice.entity.User;
import com.dmh.userservice.exception.UserNotFoundException;
import com.dmh.userservice.mapper.UserDtoMapper;
import com.dmh.userservice.provider.account.AccountProvider;
import com.dmh.userservice.repository.UserRepository;
import com.dmh.userservice.service.user.UserLogoutService;
import com.dmh.userservice.service.user.UserRegistrationService;
import com.dmh.userservice.service.user.UserUpdateService;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRegistrationService registrationService;
    private final UserUpdateService updateService;
    private final UserLogoutService logoutService;
    private final UserRepository userRepository;
    private final AccountProvider accountProvider;
    private final UserDtoMapper mapper;

    public UserService(
            UserRegistrationService registrationService,
            UserUpdateService updateService,
            UserLogoutService logoutService,
            UserRepository userRepository,
            AccountProvider accountProvider,
            UserDtoMapper mapper) {
        this.registrationService = registrationService;
        this.updateService = updateService;
        this.logoutService = logoutService;
        this.userRepository = userRepository;
        this.accountProvider = accountProvider;
        this.mapper = mapper;
    }

    // Registration
    public UserResponseDto register(UserRegisterRequestDto userDto) {
        return registrationService.registerUser(userDto);
    }

    // Update
    public UserResponseDto updateUser(Long id, UserUpdateRequestDto userRequest){
        return updateService.updateUser(id, userRequest);
    }

    // Logout
    public void logout(LogoutRequestDto logoutRequest) {
        logoutService.logout(logoutRequest);
    }

    // Find by email
    public UserResponseDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return mapper.toUserResponseDto(user);
    }

    // Find by id
    public UserResponseDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        AccountResponseDto accountResponseDto = accountProvider.findAccountByUserId(id);
        return mapper.toUserResponseDto(user, accountResponseDto);
    }


}
