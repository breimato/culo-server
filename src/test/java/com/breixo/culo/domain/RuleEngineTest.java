package com.breixo.culo.domain;

import com.breixo.culo.domain.model.Card;
import com.breixo.culo.domain.model.CardRank;
import com.breixo.culo.domain.model.Play;
import com.breixo.culo.domain.model.Round;
import com.breixo.culo.domain.model.Suit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleEngineTest {

    private RuleEngine ruleEngine;

    @BeforeEach
    void setUp() {
        this.ruleEngine = new RuleEngine();
    }

    @Test
    void testIsLegal_whenRoundOpen_thenAnyValidPlayIsLegal() {
        final var round = new Round();
        final var play = makePair(3);

        assertTrue(this.ruleEngine.isLegal(play, round));
    }

    @Test
    void testIsLegal_whenAsOros_thenAlwaysLegal() {
        final var round = closedRoundWithPair(12);
        final var asOros = Play.builder()
                .cards(List.of(Card.builder().suit(Suit.OROS).number(1).build()))
                .build();

        assertTrue(this.ruleEngine.isLegal(asOros, round));
    }

    @Test
    void testIsLegal_whenWrongSize_thenIllegal() {
        final var round = closedRoundWithPair(7);
        final var singleSeven = Play.builder()
                .cards(List.of(Card.builder().suit(Suit.COPAS).number(7).build()))
                .build();

        assertFalse(this.ruleEngine.isLegal(singleSeven, round));
    }

    @Test
    void testIsLegal_whenLowerRank_thenIllegal() {
        final var round = closedRoundWithPair(10);
        final var lowerPair = makePair(7);

        assertFalse(this.ruleEngine.isLegal(lowerPair, round));
    }

    @Test
    void testIsLegal_whenSameOrHigherRankAndCorrectSize_thenLegal() {
        final var round = closedRoundWithPair(7);
        final var higherPair = makePair(10);

        assertTrue(this.ruleEngine.isLegal(higherPair, round));
    }

    @Test
    void testIsPlin_whenRoundOpen_thenFalse() {
        final var round = new Round();
        final var play = makePair(5);

        assertFalse(this.ruleEngine.isPlin(play, round));
    }

    @Test
    void testIsPlin_whenSameNumberAsLastPlay_thenTrue() {
        final var round = closedRoundWithPair(7);
        final var plin = makePair(7);

        assertTrue(this.ruleEngine.isPlin(plin, round));
    }

    @Test
    void testIsPlin_whenDifferentNumber_thenFalse() {
        final var round = closedRoundWithPair(7);
        final var different = makePair(10);

        assertFalse(this.ruleEngine.isPlin(different, round));
    }

    @Test
    void testIsRoundOver_whenRoundOpen_thenFalse() {
        final var round = new Round();

        assertFalse(this.ruleEngine.isRoundOver(round, List.of("a", "b", "c")));
    }

    @Test
    void testIsRoundOver_whenTwoPlayersAndOpponentPassed_thenOver() {
        final var round = new Round();
        round.registerPlay(makePair(7), "player-a");
        round.registerPass("player-b");

        assertTrue(this.ruleEngine.isRoundOver(round, List.of("player-a", "player-b")));
    }

    @Test
    void testIsRoundOver_whenTwoPlayersAndLastPlayerPassed_thenNotOver() {
        final var round = new Round();
        round.registerPlay(makePair(7), "player-a");
        round.registerPass("player-a");

        assertFalse(this.ruleEngine.isRoundOver(round, List.of("player-a", "player-b")));
    }

    @Test
    void testIsRoundOver_whenThreePlayersAndBothRespondersPassed_thenOver() {
        final var round = new Round();
        round.registerPlay(makePair(7), "player-a");
        round.registerPass("player-b");
        round.registerPass("player-c");

        assertTrue(this.ruleEngine.isRoundOver(round, List.of("player-a", "player-b", "player-c")));
    }

    @Test
    void testIsRoundOver_whenThreePlayersAndOnlyOneResponderPassed_thenNotOver() {
        final var round = new Round();
        round.registerPlay(makePair(7), "player-a");
        round.registerPass("player-b");

        assertFalse(this.ruleEngine.isRoundOver(round, List.of("player-a", "player-b", "player-c")));
    }

    @Test
    void testIsRoundOver_whenThreePlayersAndLastPlayerPassedAmongOthers_thenStillNeedsOtherPass() {
        final var round = new Round();
        round.registerPlay(makePair(7), "player-a");
        round.registerPass("player-a");
        round.registerPass("player-b");

        assertFalse(this.ruleEngine.isRoundOver(round, List.of("player-a", "player-b", "player-c")));
    }

    @Test
    void testIsRoundOver_whenPlinInTwoPlayersAndOpponentSkipped_thenOverImmediately() {
        final var round = new Round();
        round.registerPlay(makePair(7), "player-a");
        round.registerPlinPlay(makePair(7), "player-b", "player-a");

        assertTrue(this.ruleEngine.isRoundOver(round, List.of("player-a", "player-b")));
    }

    @Test
    void testIsRoundOver_whenPlinInThreePlayersAndResponderPassed_thenOver() {
        final var round = new Round();
        round.registerPlay(makePair(7), "player-a");
        round.registerPlinPlay(makePair(7), "player-b", "player-c");
        round.registerPass("player-a");

        assertTrue(this.ruleEngine.isRoundOver(round, List.of("player-a", "player-b", "player-c")));
    }

    @Test
    void testIsRoundOver_whenPlinInThreePlayersAndOnlySkippedPlayerPassed_thenNotOver() {
        final var round = new Round();
        round.registerPlay(makePair(7), "player-a");
        round.registerPlinPlay(makePair(7), "player-b", "player-c");
        round.registerPass("player-c");

        assertFalse(this.ruleEngine.isRoundOver(round, List.of("player-a", "player-b", "player-c")));
    }

    private static Round closedRoundWithPair(final int number) {
        final var round = new Round();
        round.registerPlay(makePair(number), "player-a");
        return round;
    }

    private static Play makePair(final int number) {
        return Play.builder()
                .cards(List.of(
                        Card.builder().suit(Suit.COPAS).number(number).build(),
                        Card.builder().suit(Suit.ESPADAS).number(number).build()))
                .build();
    }
}
