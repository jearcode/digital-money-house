package com.dmh.userservice.repository;

import com.dmh.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByDni(String dni);

    Optional<User> findByEmail(String email);

    Optional<User> findByDni(String dni);

}
