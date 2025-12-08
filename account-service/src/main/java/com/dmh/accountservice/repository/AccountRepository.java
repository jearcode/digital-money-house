package com.dmh.accountservice.repository;

import com.dmh.accountservice.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUserId(Long userId);

    boolean existsAccountByAlias(String alias);

}
