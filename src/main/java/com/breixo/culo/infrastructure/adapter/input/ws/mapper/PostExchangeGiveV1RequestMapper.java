package com.breixo.culo.infrastructure.adapter.input.ws.mapper;

import com.breixo.culo.domain.command.game.CardInput;
import com.breixo.culo.domain.command.game.ExchangeGiveCommand;
import com.breixo.culo.domain.model.Suit;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CardV1Dto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostExchangeGiveV1RequestDto;
import org.mapstruct.Mapper;

import java.util.List;

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
    if (cardV1Dtos == null) {
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
