package com.dmh.userservice.validator;

import com.dmh.userservice.entity.User;
import com.dmh.userservice.exception.RateLimitException;
import com.dmh.userservice.exception.UserAlreadyExistException;
import com.dmh.userservice.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserUpdateValidator {

    private final UserRepository userRepository;

    public UserUpdateValidator (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateEmailUpdate(String newEmail, User currentUser){

        if (currentUser.getLastEmailUpdate() != null) {
            LocalDateTime availableDate = currentUser.getLastEmailUpdate().plusDays(15);
            if (LocalDateTime.now().isBefore(availableDate)) {
                throw new RateLimitException(
                        "You cannot change your email yet. Available from: " + availableDate
                );
            }
        }

        if (userRepository.existsByEmail(newEmail)) {
            throw new UserAlreadyExistException("The email " + newEmail + " is already in use.");
        }
    }

    public void validateDniUpdate(String newDni, Long currentUserId){
        userRepository.findByDni(newDni).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(currentUserId)) {
                throw new UserAlreadyExistException(
                        "The DNI " + newDni + " is already registered to another user."
                );
            }
        });
    }

    public void validateUpdate(
            User currentUser,
            String newEmail,
            String newDni){

        if (newEmail != null && !newEmail.equals(currentUser.getEmail())) {
            validateEmailUpdate(newEmail, currentUser);
        }

        if (newDni != null && !newDni.equals(currentUser.getDni())) {
            validateDniUpdate(newDni, currentUser.getId());
        }
    }

}
