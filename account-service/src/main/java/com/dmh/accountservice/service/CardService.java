package com.dmh.accountservice.service;

import com.dmh.accountservice.dto.request.CardCreateRequestDto;
import com.dmh.accountservice.dto.response.CardResponseDto;
import com.dmh.accountservice.entity.Card;
import com.dmh.accountservice.exception.CardNotFoundException;
import com.dmh.accountservice.mapper.CardDtoMapper;
import com.dmh.accountservice.repository.CardRepository;
import com.dmh.accountservice.validator.CardRegistrationValidator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CardService {

    private final CardRepository cardRepository;
    private final CardRegistrationValidator validator;
    private final CardDtoMapper mapper;

    public CardService(
            CardRepository cardRepository,
            CardRegistrationValidator validator,
            CardDtoMapper mapper) {
        this.cardRepository = cardRepository;
        this.validator = validator;
        this.mapper = mapper;
    }

    public CardResponseDto addCard(Long accountId, CardCreateRequestDto createRequest) throws Exception {

        validator.validateCardNumber(createRequest.getNumber());

        validator.validateExpirationDate(createRequest.getExpirationDate());

        Card card = mapper.toCardEntity(createRequest, accountId);

        Card savedCard = cardRepository.save(card);

        return mapper.toCardResponseDto(savedCard);
    }

    public List<CardResponseDto> getCardsByAccountId(Long accountId) {
        return cardRepository.findAllByAccountId(accountId)
                .stream()
                .map(mapper::toCardResponseDto)
                .collect(Collectors.toList());
    }

    public CardResponseDto getCardById(Long cardId, Long accountId) {
        Card card = cardRepository.findByIdAndAccountId(cardId, accountId)
                .orElseThrow(() -> new CardNotFoundException(cardId, accountId));
        return mapper.toCardResponseDto(card);
    }

    public void deleteCard(Long cardId, Long accountId) {
        Card card = cardRepository.findByIdAndAccountId(cardId, accountId)
                .orElseThrow(() -> new CardNotFoundException(cardId, accountId));

        cardRepository.delete(card);
    }
}