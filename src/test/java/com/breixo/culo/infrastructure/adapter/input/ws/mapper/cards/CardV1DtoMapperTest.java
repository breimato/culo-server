package com.breixo.culo.infrastructure.adapter.input.ws.mapper.cards;

import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.cards.enums.Suit;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.SuitV1Dto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * The Class CardV1DtoMapperTest.
 */
@ExtendWith(MockitoExtension.class)
class CardV1DtoMapperTest {

    /** The card V 1 dto mapper. */
    @InjectMocks
    CardV1DtoMapperImpl cardV1DtoMapper;

    /**
	 * Test to card V 1 dto when card is valid then return mapped dto.
	 */
    @Test
    void testToCardV1Dto_whenCardIsValid_thenReturnMappedDto() {
        
        // Given
        final var card = Card.builder().suit(Suit.OROS).number(7).build();

        // When
        final var cardV1Dto = this.cardV1DtoMapper.toCardV1Dto(card);

        // Then
        assertEquals(SuitV1Dto.OROS, cardV1Dto.getSuit());
        assertEquals(7, cardV1Dto.getNumber());
    }

    /**
	 * Test to card V 1 dto when card is null then return null.
	 */
    @Test
    void testToCardV1Dto_whenCardIsNull_thenReturnNull() {
        // When / Then
        assertNull(this.cardV1DtoMapper.toCardV1Dto(null));
    }
}
