package com.dmh.accountservice.service;

import com.dmh.accountservice.dto.AccountDto;
import com.dmh.accountservice.entity.Account;
import com.dmh.accountservice.exception.UserAlreadyHasAccountException;
import com.dmh.accountservice.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final String[] WORDS = { "BeanRx", "JavRex", "SrvCore", "ObjFlow", "StrmFx", "HeapFx", "ThrdX", "ByteOps", "JaxRun", "BeanFx", "RsrcIO", "MiniGC", "JetFlow", "SyncUp", "FluxIO", "NPEKing", "Log4U", "GCByte", "StackR", "ProcFx", "SrvX", "ByteX", "CoreIO" };

    public AccountService (AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountDto createAccount (Long userId) {

        if (accountRepository.findByUserId(userId).isPresent()) {
            throw new UserAlreadyHasAccountException("User with ID " + userId + " already has a registered account.");
        }

        String cvu = generateCvu();
        String alias = generateAlias();

        Account account = Account.builder()
                .userId(userId)
                .cvu(cvu)
                .alias(alias)
                .balance(BigDecimal.ZERO)
                .build();

        Account savedAccount = accountRepository.save(account);

        return mapToDto(savedAccount);


    }

    private String generateCvu() {

        Random random = new Random();
        StringBuilder cvu = new StringBuilder();
        for (int i = 0; i < 22; i++) {
            cvu.append(random.nextInt(10));
        }
        return cvu.toString();

    }

    private String generateAlias () {
        Random random = new Random();
        String w1 = WORDS[random.nextInt(WORDS.length)];
        String w2 = WORDS[random.nextInt(WORDS.length)];
        String w3 = WORDS[random.nextInt(WORDS.length)];
        return w1 + "." + w2 + "." + w3;
    }

    private AccountDto mapToDto (Account account) {

        return AccountDto.builder()
                .id(account.getId())
                .userId(account.getUserId())
                .cvu(account.getCvu())
                .alias(account.getAlias())
                .balance(account.getBalance())
                .build();

    }

}
