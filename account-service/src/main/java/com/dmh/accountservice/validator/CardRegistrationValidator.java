package com.dmh.accountservice.validator;

import com.dmh.accountservice.repository.CardRepository;
import com.dmh.accountservice.exception.CardAlreadyExistException;
import org.springframework.stereotype.Component;


@Component
public class CardRegistrationValidator {

    private final CardRepository cardRepository;

    public CardRegistrationValidator(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public void validateCardNumber(String cardNumber) throws Exception {
        if (cardRepository.existsByNumber(cardNumber)) {
            throw new CardAlreadyExistException(
                    "The card with number ending in " + cardNumber.substring(cardNumber.length() - 4) +
                            " is already registered to another account."
            );
        }
    }

    public void validateExpirationDate(String expirationDate) throws Exception {
        String[] parts = expirationDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt("20" + parts[1]);

        java.time.YearMonth cardYearMonth = java.time.YearMonth.of(year, month);
        java.time.YearMonth now = java.time.YearMonth.now();

        if (cardYearMonth.isBefore(now)) {
            throw new IllegalArgumentException("The card has expired");
        }
    }
}