package com.dmh.accountservice.repository;

import com.dmh.accountservice.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    boolean existsByNumber(String cardNumber);

    List<Card> findAllByAccountId(Long accountId);

    Optional<Card> findByIdAndAccountId(Long cardId, Long accountId);
}
