package com.breixo.culo.domain.service.game;

import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.cards.enums.CardRank;
import com.breixo.culo.domain.model.cards.enums.Suit;
import com.breixo.culo.domain.model.game.Play;
import com.breixo.culo.domain.model.game.Round;
import com.breixo.culo.domain.port.input.cards.CardRankResolverService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The Class RoundServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
class RoundServiceImplTest {

    /** The round service. */
    @InjectMocks
    RoundServiceImpl roundService;

    /** The card rank resolver service. */
    @Mock
    CardRankResolverService cardRankResolverService;

    /**
	 * Test register play when play is valid then update round state.
	 */
    @Test
    void testRegisterPlay_whenPlayIsValid_thenUpdateRoundState() {
        
        // Given
        final var card = Card.builder().suit(Suit.OROS).number(7).build();
        final var cardOne = Card.builder().suit(Suit.COPAS).number(7).build();
        final var play = Play.builder().cards(List.of(card, cardOne)).build();
        final var round = Round.builder()
                .requirement(0)
                .playersPassedSinceLastPlay(new HashSet<>())
                .lastPlayedCards(List.of())
                .build();

        // When
        when(this.cardRankResolverService.resolve(card)).thenReturn(CardRank.SIETE);
        final var updatedRound = this.roundService.registerPlay(round, play, "player-a");

        // Then
        verify(this.cardRankResolverService, times(1)).resolve(card);
        assertEquals(2, updatedRound.requirement());
        assertEquals(CardRank.SIETE, updatedRound.lastRank());
        assertEquals(7, updatedRound.lastCardNumber());
        assertEquals("player-a", updatedRound.lastPlayerId());
        assertTrue(updatedRound.playersPassedSinceLastPlay().isEmpty());
    }

    /**
	 * Test reset when round has state then clear round.
	 */
    @Test
    void testReset_whenRoundHasState_thenClearRound() {
        
        // Given
        final var round = Round.builder()
                .requirement(2)
                .lastRank(CardRank.SIETE)
                .lastCardNumber(7)
                .lastPlayerId("player-a")
                .playersPassedSinceLastPlay(new HashSet<>())
                .lastPlayedCards(List.of())
                .build();

        // When
        final var resetRound = this.roundService.reset(round);

        // Then
        assertEquals(0, resetRound.requirement());
        assertNull(resetRound.lastRank());
        assertTrue(resetRound.playersPassedSinceLastPlay().isEmpty());
    }
}
