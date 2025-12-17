package com.dmh.accountservice.service;

import com.dmh.accountservice.exception.UnsupportedCardTypeException;
import com.dmh.accountservice.model.enums.CardType;
import com.dmh.accountservice.provider.DepositProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepositProviderStrategy {

    private final List<DepositProvider> providers;

    public DepositProviderStrategy (List<DepositProvider> providers) {
        this.providers = providers;
    }

    public DepositProvider getProvider (CardType cardType) {

        return providers.stream()
                .filter(provider -> provider.supports(cardType))
                .findFirst()
                .orElseThrow(() -> new UnsupportedCardTypeException(cardType));

    }

}
