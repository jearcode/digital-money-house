package com.dmh.userservice.validator;

import com.dmh.userservice.dto.request.UserRegisterRequestDto;
import com.dmh.userservice.exception.UserAlreadyExistException;
import com.dmh.userservice.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationValidator {

    private final UserRepository userRepository;

    public UserRegistrationValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateRegistration(UserRegisterRequestDto userDto){
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistException("Email is already registered: " + userDto.getEmail());
        }
        if (userRepository.existsByDni(userDto.getDni())) {
            throw new UserAlreadyExistException("DNI is already registered: " + userDto.getDni());
        }
    }

}
