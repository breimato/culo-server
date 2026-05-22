package com.breixo.culo.infrastructure.adapter.input.ws.mapper.swap;

import com.breixo.culo.domain.command.cards.CardInput;
import com.breixo.culo.domain.command.swap.ExchangeGiveCommand;
import com.breixo.culo.domain.model.cards.enums.Suit;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CardV1Dto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostExchangeGiveV1RequestDto;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Objects;

/**
 * The Interface PostExchangeGiveV1RequestMapper.
 */
@Mapper(componentModel = "spring")
public interface PostExchangeGiveV1RequestMapper {

  /**
	 * To exchange give command.
	 *
	 * @param postExchangeGiveV1RequestDto the post exchange give V 1 request dto
	 * @return the exchange give command
	 */
  ExchangeGiveCommand toExchangeGiveCommand(PostExchangeGiveV1RequestDto postExchangeGiveV1RequestDto);

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
