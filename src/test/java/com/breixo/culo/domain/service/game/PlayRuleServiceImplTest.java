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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/** The Class Play Rule Service Impl Test. */
@ExtendWith(MockitoExtension.class)
class PlayRuleServiceImplTest {

    /** The play rule service. */
    @InjectMocks
    PlayRuleServiceImpl playRuleService;

    /** The card rank resolver service. */
    @Mock
    CardRankResolverService cardRankResolverService;

    /** Test is legal when round open then any valid play is legal. */
    @Test
    void testIsLegal_whenRoundOpen_thenAnyValidPlayIsLegal() {
        // Given
        final var round = Round.builder()
                .requirement(0)
                .lastRank(null)
                .lastCardNumber(0)
                .playersPassedSinceLastPlay(new HashSet<>())
                .lastPlayedCards(List.of())
                .build();
        final var play = Play.builder()
                .cards(List.of(
                        Card.builder().suit(Suit.COPAS).number(3).build(),
                        Card.builder().suit(Suit.ESPADAS).number(3).build()))
                .build();

        // When / Then
        assertTrue(this.playRuleService.isLegal(play, round));
    }

    /** Test is legal when as oros then always legal. */
    @Test
    void testIsLegal_whenAsOros_thenAlwaysLegal() {
        // Given
        final var cardCopas = Card.builder().suit(Suit.COPAS).number(12).build();
        final var cardEspadas = Card.builder().suit(Suit.ESPADAS).number(12).build();
        final var round = Round.builder()
                .requirement(2)
                .lastRank(CardRank.REY)
                .lastCardNumber(12)
                .lastPlayerId("player-a")
                .playersPassedSinceLastPlay(new HashSet<>())
                .lastPlayedCards(List.of(cardCopas, cardEspadas))
                .build();
        final var asOros = Play.builder()
                .cards(List.of(Card.builder().suit(Suit.OROS).number(1).build()))
                .build();

        // When / Then
        assertTrue(this.playRuleService.isLegal(asOros, round));
    }

    /** Test is legal when wrong size then illegal. */
    @Test
    void testIsLegal_whenWrongSize_thenIllegal() {
        // Given
        final var cardCopas = Card.builder().suit(Suit.COPAS).number(7).build();
        final var cardEspadas = Card.builder().suit(Suit.ESPADAS).number(7).build();
        final var round = Round.builder()
                .requirement(2)
                .lastRank(CardRank.SIETE)
                .lastCardNumber(7)
                .lastPlayerId("player-a")
                .playersPassedSinceLastPlay(new HashSet<>())
                .lastPlayedCards(List.of(cardCopas, cardEspadas))
                .build();
        final var singleSeven = Play.builder()
                .cards(List.of(Card.builder().suit(Suit.COPAS).number(7).build()))
                .build();

        // When / Then
        assertFalse(this.playRuleService.isLegal(singleSeven, round));
    }

    /** Test is legal when lower rank then illegal. */
    @Test
    void testIsLegal_whenLowerRank_thenIllegal() {
        // Given
        final var cardCopas = Card.builder().suit(Suit.COPAS).number(10).build();
        final var cardEspadas = Card.builder().suit(Suit.ESPADAS).number(10).build();
        final var round = Round.builder()
                .requirement(2)
                .lastRank(CardRank.SOTA)
                .lastCardNumber(10)
                .lastPlayerId("player-a")
                .playersPassedSinceLastPlay(new HashSet<>())
                .lastPlayedCards(List.of(cardCopas, cardEspadas))
                .build();
        final var lowerPair = Play.builder()
                .cards(List.of(
                        Card.builder().suit(Suit.COPAS).number(7).build(),
                        Card.builder().suit(Suit.ESPADAS).number(7).build()))
                .build();
        when(this.cardRankResolverService.resolve(lowerPair.cards().getFirst())).thenReturn(CardRank.SIETE);

        // When / Then
        assertFalse(this.playRuleService.isLegal(lowerPair, round));
    }

    /** Test is legal when same or higher rank and correct size then legal. */
    @Test
    void testIsLegal_whenSameOrHigherRankAndCorrectSize_thenLegal() {
        // Given
        final var cardCopas = Card.builder().suit(Suit.COPAS).number(7).build();
        final var cardEspadas = Card.builder().suit(Suit.ESPADAS).number(7).build();
        final var round = Round.builder()
                .requirement(2)
                .lastRank(CardRank.SIETE)
                .lastCardNumber(7)
                .lastPlayerId("player-a")
                .playersPassedSinceLastPlay(new HashSet<>())
                .lastPlayedCards(List.of(cardCopas, cardEspadas))
                .build();
        final var higherPair = Play.builder()
                .cards(List.of(
                        Card.builder().suit(Suit.COPAS).number(10).build(),
                        Card.builder().suit(Suit.ESPADAS).number(10).build()))
                .build();
        when(this.cardRankResolverService.resolve(higherPair.cards().getFirst())).thenReturn(CardRank.SOTA);

        // When / Then
        assertTrue(this.playRuleService.isLegal(higherPair, round));
    }

    /** Test is plin when round open then false. */
    @Test
    void testIsPlin_whenRoundOpen_thenFalse() {
        // Given
        final var round = Round.builder()
                .requirement(0)
                .lastRank(null)
                .lastCardNumber(0)
                .playersPassedSinceLastPlay(new HashSet<>())
                .lastPlayedCards(List.of())
                .build();
        final var play = Play.builder()
                .cards(List.of(
                        Card.builder().suit(Suit.COPAS).number(5).build(),
                        Card.builder().suit(Suit.ESPADAS).number(5).build()))
                .build();

        // When / Then
        assertFalse(this.playRuleService.isPlin(play, round));
    }

    /** Test is plin when same number as last play then true. */
    @Test
    void testIsPlin_whenSameNumberAsLastPlay_thenTrue() {
        // Given
        final var cardCopas = Card.builder().suit(Suit.COPAS).number(7).build();
        final var cardEspadas = Card.builder().suit(Suit.ESPADAS).number(7).build();
        final var round = Round.builder()
                .requirement(2)
                .lastRank(CardRank.SIETE)
                .lastCardNumber(7)
                .lastPlayerId("player-a")
                .playersPassedSinceLastPlay(new HashSet<>())
                .lastPlayedCards(List.of(cardCopas, cardEspadas))
                .build();
        final var plin = Play.builder()
                .cards(List.of(
                        Card.builder().suit(Suit.COPAS).number(7).build(),
                        Card.builder().suit(Suit.BASTOS).number(7).build()))
                .build();

        // When / Then
        assertTrue(this.playRuleService.isPlin(plin, round));
    }

    /** Test is plin when different number then false. */
    @Test
    void testIsPlin_whenDifferentNumber_thenFalse() {
        // Given
        final var cardCopas = Card.builder().suit(Suit.COPAS).number(7).build();
        final var cardEspadas = Card.builder().suit(Suit.ESPADAS).number(7).build();
        final var round = Round.builder()
                .requirement(2)
                .lastRank(CardRank.SIETE)
                .lastCardNumber(7)
                .lastPlayerId("player-a")
                .playersPassedSinceLastPlay(new HashSet<>())
                .lastPlayedCards(List.of(cardCopas, cardEspadas))
                .build();
        final var different = Play.builder()
                .cards(List.of(
                        Card.builder().suit(Suit.COPAS).number(10).build(),
                        Card.builder().suit(Suit.ESPADAS).number(10).build()))
                .build();

        // When / Then
        assertFalse(this.playRuleService.isPlin(different, round));
    }

    /** Test is round over when round open then false. */
    @Test
    void testIsRoundOver_whenRoundOpen_thenFalse() {
        // Given
        final var round = Round.builder()
                .requirement(0)
                .lastRank(null)
                .lastCardNumber(0)
                .playersPassedSinceLastPlay(new HashSet<>())
                .lastPlayedCards(List.of())
                .build();

        // When / Then
        assertFalse(this.playRuleService.isRoundOver(round, List.of("a", "b", "c")));
    }

    /** Test is round over when two players and opponent passed then over. */
    @Test
    void testIsRoundOver_whenTwoPlayersAndOpponentPassed_thenOver() {
        // Given
        final var round = Round.builder()
                .requirement(2)
                .lastRank(CardRank.SIETE)
                .lastCardNumber(7)
                .lastPlayerId("player-a")
                .playersPassedSinceLastPlay(Set.of("player-b"))
                .lastPlayedCards(List.of())
                .build();

        // When / Then
        assertTrue(this.playRuleService.isRoundOver(round, List.of("player-a", "player-b")));
    }

    /** Test is round over when two players and last player passed then not over. */
    @Test
    void testIsRoundOver_whenTwoPlayersAndLastPlayerPassed_thenNotOver() {
        // Given
        final var round = Round.builder()
                .requirement(2)
                .lastRank(CardRank.SIETE)
                .lastCardNumber(7)
                .lastPlayerId("player-a")
                .playersPassedSinceLastPlay(Set.of("player-a"))
                .lastPlayedCards(List.of())
                .build();

        // When / Then
        assertFalse(this.playRuleService.isRoundOver(round, List.of("player-a", "player-b")));
    }

    /** Test is round over when three players and both responders passed then over. */
    @Test
    void testIsRoundOver_whenThreePlayersAndBothRespondersPassed_thenOver() {
        // Given
        final var round = Round.builder()
                .requirement(2)
                .lastRank(CardRank.SIETE)
                .lastCardNumber(7)
                .lastPlayerId("player-a")
                .playersPassedSinceLastPlay(Set.of("player-b", "player-c"))
                .lastPlayedCards(List.of())
                .build();

        // When / Then
        assertTrue(this.playRuleService.isRoundOver(round, List.of("player-a", "player-b", "player-c")));
    }

    /** Test is round over when three players and only one responder passed then not over. */
    @Test
    void testIsRoundOver_whenThreePlayersAndOnlyOneResponderPassed_thenNotOver() {
        // Given
        final var round = Round.builder()
                .requirement(2)
                .lastRank(CardRank.SIETE)
                .lastCardNumber(7)
                .lastPlayerId("player-a")
                .playersPassedSinceLastPlay(Set.of("player-b"))
                .lastPlayedCards(List.of())
                .build();

        // When / Then
        assertFalse(this.playRuleService.isRoundOver(round, List.of("player-a", "player-b", "player-c")));
    }

    /** Test is round over when plin in two players and opponent skipped then over immediately. */
    @Test
    void testIsRoundOver_whenPlinInTwoPlayersAndOpponentSkipped_thenOverImmediately() {
        // Given
        final var round = Round.builder()
                .requirement(2)
                .lastRank(CardRank.SIETE)
                .lastCardNumber(7)
                .lastPlayerId("player-b")
                .skippedPlayerId("player-a")
                .playersPassedSinceLastPlay(new HashSet<>())
                .lastPlayedCards(List.of())
                .build();

        // When / Then
        assertTrue(this.playRuleService.isRoundOver(round, List.of("player-a", "player-b")));
    }

    /** Test is round over when plin in three players and responder passed then over. */
    @Test
    void testIsRoundOver_whenPlinInThreePlayersAndResponderPassed_thenOver() {
        // Given
        final var round = Round.builder()
                .requirement(2)
                .lastRank(CardRank.SIETE)
                .lastCardNumber(7)
                .lastPlayerId("player-b")
                .skippedPlayerId("player-c")
                .playersPassedSinceLastPlay(Set.of("player-a"))
                .lastPlayedCards(List.of())
                .build();

        // When / Then
        assertTrue(this.playRuleService.isRoundOver(round, List.of("player-a", "player-b", "player-c")));
    }

    /** Test is round over when plin in three players and only skipped player passed then not over. */
    @Test
    void testIsRoundOver_whenPlinInThreePlayersAndOnlySkippedPlayerPassed_thenNotOver() {
        // Given
        final var round = Round.builder()
                .requirement(2)
                .lastRank(CardRank.SIETE)
                .lastCardNumber(7)
                .lastPlayerId("player-b")
                .skippedPlayerId("player-c")
                .playersPassedSinceLastPlay(Set.of("player-c"))
                .lastPlayedCards(List.of())
                .build();

        // When / Then
        assertFalse(this.playRuleService.isRoundOver(round, List.of("player-a", "player-b", "player-c")));
    }
}
