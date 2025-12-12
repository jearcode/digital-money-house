package com.dmh.accountservice.provider;

import com.dmh.accountservice.entity.Card;
import com.dmh.accountservice.model.enums.CardType;
import com.dmh.accountservice.repository.AccountRepository;
import com.dmh.accountservice.repository.TransactionRepository;
import org.springframework.stereotype.Component;

@Component
public class CreditCardDepositProvider extends AbstractCardDepositProvider{

    protected CreditCardDepositProvider(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        super(transactionRepository, accountRepository);
    }

    @Override
    protected String getOriginName() {
        return CardType.CREDIT_CARD.toString();
    }

    @Override
    protected String generateDescription(Card card) {
        String lastFourDigits = card.getNumber().substring(card.getNumber().length() - 4);
        return "Deposit with credit card ends in: " + lastFourDigits;
    }

    @Override
    public boolean supports(CardType cardType) {
        return cardType == CardType.CREDIT_CARD;
    }
}
