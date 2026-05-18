package com.breixo.culo.infrastructure.adapter.input.ws.mapper;

import com.breixo.culo.domain.model.Card;
import com.breixo.culo.domain.model.CardRank;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CardDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CardRankNameDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.SuitDto;
import com.breixo.culo.domain.model.Suit;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CardDtoMapper {

  CardDto toCardDto(Card card);

  List<CardDto> toCardDtoList(List<Card> cards);

  Card toCard(CardDto cardDto);

  List<Card> toCardList(List<CardDto> cardDtos);

  @Named("suitToDto")
  default SuitDto toSuitDto(final Suit suit) {
    return SuitDto.valueOf(suit.name());
  }

  @Named("suitFromDto")
  default Suit toSuit(final SuitDto suitDto) {
    return Suit.valueOf(suitDto.name());
  }

  default CardRankNameDto toCardRankNameDto(final CardRank cardRank) {
    if (cardRank == null) {
      return null;
    }
    return CardRankNameDto.valueOf(cardRank.name());
  }
}
