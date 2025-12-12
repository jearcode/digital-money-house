package com.dmh.accountservice.provider;

import com.dmh.accountservice.entity.Account;
import com.dmh.accountservice.entity.Card;
import com.dmh.accountservice.entity.Transaction;
import com.dmh.accountservice.model.enums.CardType;

import java.math.BigDecimal;

public interface DepositProvider {

    Transaction processDeposit (Account account, Card card, BigDecimal amount);
    boolean supports (CardType cardType);

}
