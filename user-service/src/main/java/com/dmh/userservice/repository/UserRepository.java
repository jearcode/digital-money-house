package com.dmh.userservice.repository;

import com.dmh.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByDni(String dni);

    User findByKeycloakId(String keycloakId);


}
