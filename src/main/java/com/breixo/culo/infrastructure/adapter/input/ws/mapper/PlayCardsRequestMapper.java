package com.breixo.culo.infrastructure.adapter.input.ws.mapper;

import com.breixo.culo.domain.command.game.CardInput;
import com.breixo.culo.domain.command.game.PlayCardsCommand;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CardDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PlayCardsRequestDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlayCardsRequestMapper {

  PlayCardsCommand toPlayCardsCommand(PlayCardsRequestDto playCardsRequestDto);

  default List<CardInput> toCardInputList(final List<CardDto> cardDtos) {
    if (cardDtos == null) {
      return List.of();
    }
    return cardDtos.stream()
        .map(dto -> CardInput.builder()
            .suit(com.breixo.culo.domain.model.Suit.valueOf(dto.getSuit().name()))
            .number(dto.getNumber())
            .build())
        .toList();
  }
}
