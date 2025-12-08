package com.dmh.accountservice.mapper;

import com.dmh.accountservice.dto.request.CardCreateRequestDto;
import com.dmh.accountservice.dto.response.CardResponseDto;
import com.dmh.accountservice.entity.Card;
import org.springframework.stereotype.Component;

@Component
public class CardDtoMapper {

    public Card toCardEntity(CardCreateRequestDto requestDto, Long accountId) {
        return Card.builder()
                .accountId(accountId)
                .number(requestDto.getNumber())
                .cardholderName(requestDto.getCardholderName())
                .expirationDate(requestDto.getExpirationDate())
                .cvv(requestDto.getCvv())
                .type(requestDto.getType())
                .isActive(true)
                .build();
    }

    public CardResponseDto toCardResponseDto(Card card) {
        String lastFourDigits = card.getNumber().substring(card.getNumber().length() - 4);

        return CardResponseDto.builder()
                .id(card.getId())
                .accountId(card.getAccountId())
                .lastFourDigits(lastFourDigits)
                .cardholderName(card.getCardholderName())
                .expirationDate(card.getExpirationDate())
                .type(card.getType())
                .isActive(card.getIsActive())
                .createdAt(card.getCreatedAt())
                .build();
    }
}