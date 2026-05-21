package com.breixo.culo.infrastructure.adapter.input.ws.mapper;

import com.breixo.culo.domain.command.game.CardInput;
import com.breixo.culo.domain.command.game.PlayCardsCommand;
import com.breixo.culo.domain.model.card.enums.Suit;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CardV1Dto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostGamePlayV1RequestDto;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Objects;

/**
 * The Interface PostGamePlayV1RequestMapper.
 */
@Mapper(componentModel = "spring")
public interface PostGamePlayV1RequestMapper {

  /**
	 * To play cards command.
	 *
	 * @param postGamePlayV1RequestDto the post game play V 1 request dto
	 * @return the play cards command
	 */
  PlayCardsCommand toPlayCardsCommand(PostGamePlayV1RequestDto postGamePlayV1RequestDto);

  /**
	 * To card input list.
	 *
	 * @param cardV1Dtos the card V 1 dtos
	 * @return the list
	 */
  default List<CardInput> toCardInputList(final List<CardV1Dto> cardV1Dtos) {
    if (Objects.isNull(cardV1Dtos)) {
      return List.of();
    }
    return cardV1Dtos.stream()
        .map(cardV1Dto -> CardInput.builder()
            .suit(Suit.valueOf(cardV1Dto.getSuit().name()))
            .number(cardV1Dto.getNumber())
            .build())
        .toList();
  }
}
