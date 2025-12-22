package com.dmh.accountservice.provider;

import com.dmh.accountservice.dto.response.TransactionResponseDto;
import com.dmh.accountservice.entity.Account;
import com.dmh.accountservice.entity.Transaction;
import com.dmh.accountservice.exception.InsufficientFundsException;
import com.dmh.accountservice.mapper.TransactionDtoMapper;
import com.dmh.accountservice.model.enums.TransactionType;
import com.dmh.accountservice.repository.AccountRepository;
import com.dmh.accountservice.repository.TransactionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class TransferProvider {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionDtoMapper transactionDtoMapper;

    public TransferProvider(AccountRepository accountRepository, TransactionRepository transactionRepository, TransactionDtoMapper transactionDtoMapper) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transactionDtoMapper = transactionDtoMapper;
    }

    @Transactional
    public TransactionResponseDto processTransfer(
            Account sender, Account receipt, BigDecimal amount, String description) {

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }

        receipt.setBalance(receipt.getBalance().add(amount));
        accountRepository.save(receipt);

        sender.setBalance(sender.getBalance().subtract(amount));
        accountRepository.save(sender);

        Transaction senderTransaction = Transaction.builder()
                .accountId(sender.getId())
                .amount(amount.negate())
                .transactionType(TransactionType.TRANSFER_SENT)
                .origin("ACCOUNT")
                .destinationAccount(receipt.getCvu())
                .description(description)
                .transactionDate(LocalDateTime.now())
                .build();

        Transaction receiptTransaction = Transaction.builder()
                .accountId(receipt.getId())
                .amount(amount)
                .transactionType(TransactionType.TRANSFER_RECEIVED)
                .origin(sender.getAlias())
                .destinationAccount("ACCOUNT")
                .description(description)
                .transactionDate(LocalDateTime.now())
                .build();

        transactionRepository.save(senderTransaction);
        transactionRepository.save(receiptTransaction);

        return transactionDtoMapper.toDto(senderTransaction);
    }

}
