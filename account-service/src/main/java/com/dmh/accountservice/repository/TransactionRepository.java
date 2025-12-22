package com.dmh.accountservice.repository;

import com.dmh.accountservice.entity.Transaction;
import com.dmh.accountservice.model.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByAccountIdOrderByTransactionDateDesc(Long accountId);

    Optional<Transaction> findByAccountIdAndId(Long accountId, Long id);

    List<Transaction> findAllByAccountIdAndTransactionTypeOrderByTransactionDateDesc(
            Long accountId, TransactionType transactionType);

}
