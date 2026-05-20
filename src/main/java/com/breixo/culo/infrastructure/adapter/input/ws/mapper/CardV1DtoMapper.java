package com.breixo.culo.infrastructure.adapter.input.ws.mapper;

import com.breixo.culo.domain.model.Card;
import com.breixo.culo.domain.model.CardRank;
import com.breixo.culo.domain.model.Suit;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CardRankNameV1Dto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CardV1Dto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.SuitV1Dto;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

/**
 * The Interface CardV1DtoMapper.
 */
@Mapper(componentModel = "spring")
public interface CardV1DtoMapper {

  /**
	 * To card V 1 dto.
	 *
	 * @param card the card
	 * @return the card V 1 dto
	 */
  CardV1Dto toCardV1Dto(Card card);

  /**
	 * To card V 1 dto list.
	 *
	 * @param cards the cards
	 * @return the list
	 */
  List<CardV1Dto> toCardV1DtoList(List<Card> cards);

  /**
	 * To card.
	 *
	 * @param cardV1Dto the card V 1 dto
	 * @return the card
	 */
  Card toCard(CardV1Dto cardV1Dto);

  /**
	 * To card list.
	 *
	 * @param cardV1Dtos the card V 1 dtos
	 * @return the list
	 */
  List<Card> toCardList(List<CardV1Dto> cardV1Dtos);

  /**
	 * To suit V 1 dto.
	 *
	 * @param suit the suit
	 * @return the suit V 1 dto
	 */
  @Named("suitToDto")
  default SuitV1Dto toSuitV1Dto(final Suit suit) {
    return SuitV1Dto.valueOf(suit.name());
  }

  /**
	 * To suit.
	 *
	 * @param suitV1Dto the suit V 1 dto
	 * @return the suit
	 */
  @Named("suitFromDto")
  default Suit toSuit(final SuitV1Dto suitV1Dto) {
    return Suit.valueOf(suitV1Dto.name());
  }

  /**
	 * To card rank name V 1 dto.
	 *
	 * @param cardRank the card rank
	 * @return the card rank name V 1 dto
	 */
  default CardRankNameV1Dto toCardRankNameV1Dto(final CardRank cardRank) {
    if (cardRank == null) {
      return null;
    }
    return CardRankNameV1Dto.valueOf(cardRank.name());
  }
}
