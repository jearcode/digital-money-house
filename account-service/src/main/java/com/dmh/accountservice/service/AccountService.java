package com.dmh.accountservice.service;

import com.dmh.accountservice.dto.request.CardCreateRequestDto;
import com.dmh.accountservice.dto.request.DepositRequestDto;
import com.dmh.accountservice.dto.request.UpdateAccountRequestDto;
import com.dmh.accountservice.dto.response.AccountDto;
import com.dmh.accountservice.dto.response.CardResponseDto;
import com.dmh.accountservice.dto.response.TransactionResponseDto;
import com.dmh.accountservice.entity.Account;
import com.dmh.accountservice.entity.Card;
import com.dmh.accountservice.exception.AccountNotFoundException;
import com.dmh.accountservice.exception.AliasAlreadyExistsException;
import com.dmh.accountservice.exception.UserAlreadyHasAccountException;
import com.dmh.accountservice.mapper.AccountDtoMapper;
import com.dmh.accountservice.mapper.TransactionDtoMapper;
import com.dmh.accountservice.provider.DepositProvider;
import com.dmh.accountservice.repository.AccountRepository;
import com.dmh.accountservice.util.AccountNumberGenerator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountDtoMapper accountMapper;
    private final CardService cardService;
    private final AccountNumberGenerator numberGenerator;
    private DepositProviderStrategy depositStrategy;
    private TransactionDtoMapper transactionDtoMapper;

    public AccountService(
            AccountRepository accountRepository,
            AccountDtoMapper accountMapper,
            CardService cardService,
            AccountNumberGenerator numberGenerator,
            DepositProviderStrategy depositStrategy, TransactionDtoMapper transactionDtoMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.cardService = cardService;
        this.numberGenerator = numberGenerator;
        this.depositStrategy = depositStrategy;
        this.transactionDtoMapper = transactionDtoMapper;
    }


    public AccountDto createAccount(Long userId) {

        if (accountRepository.findByUserId(userId).isPresent()) {
            throw new UserAlreadyHasAccountException(
                    "User with ID " + userId + " already has a registered account."
            );
        }

        String cvu = numberGenerator.generateCvu();
        String alias = numberGenerator.generateAlias();

        Account account = Account.builder()
                .userId(userId)
                .cvu(cvu)
                .alias(alias)
                .balance(BigDecimal.ZERO)
                .build();

        Account savedAccount = accountRepository.save(account);

        return accountMapper.toAccountDto(savedAccount);
    }


    public AccountDto findAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        return accountMapper.toAccountDto(account);
    }


    public AccountDto findAccountByUserId(Long userId) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFoundException(userId));
        return accountMapper.toAccountDto(account);
    }

    public AccountDto updateAccount(Long id, UpdateAccountRequestDto accountRequest) {

        AccountDto accountDto = findAccountById(id);
        String newAlias = accountRequest.getAlias();


        if (accountDto.getAlias().equals(newAlias)) {
            return accountDto;
        }

        if (accountRepository.existsAccountByAlias(newAlias)) {
            throw new AliasAlreadyExistsException(newAlias);
        }

        Account account = accountMapper.toAccountEntity(accountDto);
        account.setAlias(newAlias);

        return accountMapper.toAccountDto(accountRepository.save(account));

    }

    // ============ CARDS ============


    public CardResponseDto addCardToAccount(Long accountId, CardCreateRequestDto createRequest) throws Exception {

        findAccountById(accountId);

        return cardService.addCard(accountId, createRequest);
    }


    public List<CardResponseDto> getAccountCards(Long accountId) {

        findAccountById(accountId);

        return cardService.getCardsByAccountId(accountId);
    }


    public CardResponseDto getAccountCard(Long accountId, Long cardId) {
        // Validate account exists
        findAccountById(accountId);

        return cardService.getCardById(cardId, accountId);
    }


    public void deleteAccountCard(Long accountId, Long cardId) {
        // Validate account exists
        findAccountById(accountId);

        cardService.deleteCard(cardId, accountId);
    }

    public TransactionResponseDto processDeposit (Long id, DepositRequestDto depositRequest) {

        Account account = accountMapper.toAccountEntity(findAccountById(id));

        Card card = cardService.getCardEntityById(depositRequest.getCardId(), account.getId());

        DepositProvider provider = depositStrategy.getProvider(card.getType());

        return transactionDtoMapper.toDto(provider.processDeposit(account, card, depositRequest.getAmount()));

    }

}