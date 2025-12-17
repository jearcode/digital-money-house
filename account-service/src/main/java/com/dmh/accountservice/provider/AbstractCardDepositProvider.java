package com.dmh.accountservice.provider;

import com.dmh.accountservice.entity.Account;
import com.dmh.accountservice.entity.Card;
import com.dmh.accountservice.entity.Transaction;
import com.dmh.accountservice.model.enums.TransactionType;
import com.dmh.accountservice.repository.AccountRepository;
import com.dmh.accountservice.repository.TransactionRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class AbstractCardDepositProvider implements DepositProvider{

    protected final TransactionRepository transactionRepository;
    protected final AccountRepository accountRepository;

    protected AbstractCardDepositProvider(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    protected abstract String getOriginName();
    protected abstract String generateDescription(Card card);

    @Override
    @Transactional
    public Transaction processDeposit(Account account, Card card, BigDecimal amount) {
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        String description = generateDescription(card);

        Transaction transaction = Transaction.builder()
                .accountId(account.getId())
                .amount(amount)
                .transactionType(TransactionType.DEPOSIT)
                .origin(getOriginName())
                .description(description)
                .transactionDate(LocalDateTime.now())
                .destinationAccount(null)
                .build();

        return transactionRepository.save(transaction);
    }


}
