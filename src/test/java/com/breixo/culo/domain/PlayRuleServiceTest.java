package com.breixo.culo.domain;

import com.breixo.culo.domain.model.card.Card;
import com.breixo.culo.domain.model.card.enums.Suit;
import com.breixo.culo.domain.model.play.Play;
import com.breixo.culo.domain.model.play.Round;
import com.breixo.culo.domain.service.card.CardFactoryServiceImpl;
import com.breixo.culo.domain.service.card.CardRankResolverServiceImpl;
import com.breixo.culo.domain.service.play.PlayBuilderServiceImpl;
import com.breixo.culo.domain.service.play.PlayRuleServiceImpl;
import com.breixo.culo.domain.service.round.RoundServiceImpl;
import com.breixo.culo.testsupport.RoomTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The Class PlayRuleServiceTest. */
class PlayRuleServiceTest {

    /** The play rule service. */
    private PlayRuleServiceImpl playRuleService;

    /** The round service. */
    private RoundServiceImpl roundService;

    /** The play builder service. */
    private PlayBuilderServiceImpl playBuilderService;

    /** The card factory service. */
    private CardFactoryServiceImpl cardFactoryService;

    /** Sets the up. */
    @BeforeEach
    void setUp() {
        this.cardFactoryService = new CardFactoryServiceImpl();
        final var cardRankResolverService = new CardRankResolverServiceImpl();
        this.playBuilderService = new PlayBuilderServiceImpl(this.cardFactoryService);
        this.roundService = new RoundServiceImpl(cardRankResolverService);
        this.playRuleService = new PlayRuleServiceImpl(cardRankResolverService);
    }

    /** Test is legal when round open then any valid play is legal. */
    @Test
    void testIsLegal_whenRoundOpen_thenAnyValidPlayIsLegal() {
        final var round = this.roundService.reset(RoomTestFactory.emptyRound());
        final var play = this.makePair(3);

        assertTrue(this.playRuleService.isLegal(play, round));
    }

    /** Test is legal when as oros then always legal. */
    @Test
    void testIsLegal_whenAsOros_thenAlwaysLegal() {
        final var round = this.closedRoundWithPair(12);
        final var asOros = this.playBuilderService.buildPlay(
                List.of(this.cardFactoryService.buildCard(Suit.OROS, 1)));

        assertTrue(this.playRuleService.isLegal(asOros, round));
    }

    /** Test is legal when wrong size then illegal. */
    @Test
    void testIsLegal_whenWrongSize_thenIllegal() {
        final var round = this.closedRoundWithPair(7);
        final var singleSeven = this.playBuilderService.buildPlay(
                List.of(this.cardFactoryService.buildCard(Suit.COPAS, 7)));

        assertFalse(this.playRuleService.isLegal(singleSeven, round));
    }

    /** Test is legal when lower rank then illegal. */
    @Test
    void testIsLegal_whenLowerRank_thenIllegal() {
        final var round = this.closedRoundWithPair(10);
        final var lowerPair = this.makePair(7);

        assertFalse(this.playRuleService.isLegal(lowerPair, round));
    }

    /** Test is legal when same or higher rank and correct size then legal. */
    @Test
    void testIsLegal_whenSameOrHigherRankAndCorrectSize_thenLegal() {
        final var round = this.closedRoundWithPair(7);
        final var higherPair = this.makePair(10);

        assertTrue(this.playRuleService.isLegal(higherPair, round));
    }

    /** Test is plin when round open then false. */
    @Test
    void testIsPlin_whenRoundOpen_thenFalse() {
        final var round = this.roundService.reset(RoomTestFactory.emptyRound());
        final var play = this.makePair(5);

        assertFalse(this.playRuleService.isPlin(play, round));
    }

    /** Test is plin when same number as last play then true. */
    @Test
    void testIsPlin_whenSameNumberAsLastPlay_thenTrue() {
        final var round = this.closedRoundWithPair(7);
        final var plin = this.makePair(7);

        assertTrue(this.playRuleService.isPlin(plin, round));
    }

    /** Test is plin when different number then false. */
    @Test
    void testIsPlin_whenDifferentNumber_thenFalse() {
        final var round = this.closedRoundWithPair(7);
        final var different = this.makePair(10);

        assertFalse(this.playRuleService.isPlin(different, round));
    }

    /** Test is round over when round open then false. */
    @Test
    void testIsRoundOver_whenRoundOpen_thenFalse() {
        final var round = this.roundService.reset(RoomTestFactory.emptyRound());

        assertFalse(this.playRuleService.isRoundOver(round, List.of("a", "b", "c")));
    }

    /** Test is round over when two players and opponent passed then over. */
    @Test
    void testIsRoundOver_whenTwoPlayersAndOpponentPassed_thenOver() {
        var round = this.roundService.registerPlay(RoomTestFactory.emptyRound(), this.makePair(7), "player-a");
        round = this.roundService.registerPass(round, "player-b");

        assertTrue(this.playRuleService.isRoundOver(round, List.of("player-a", "player-b")));
    }

    /** Test is round over when two players and last player passed then not over. */
    @Test
    void testIsRoundOver_whenTwoPlayersAndLastPlayerPassed_thenNotOver() {
        var round = this.roundService.registerPlay(RoomTestFactory.emptyRound(), this.makePair(7), "player-a");
        round = this.roundService.registerPass(round, "player-a");

        assertFalse(this.playRuleService.isRoundOver(round, List.of("player-a", "player-b")));
    }

    /** Test is round over when three players and both responders passed then over. */
    @Test
    void testIsRoundOver_whenThreePlayersAndBothRespondersPassed_thenOver() {
        var round = this.roundService.registerPlay(RoomTestFactory.emptyRound(), this.makePair(7), "player-a");
        round = this.roundService.registerPass(round, "player-b");
        round = this.roundService.registerPass(round, "player-c");

        assertTrue(this.playRuleService.isRoundOver(round, List.of("player-a", "player-b", "player-c")));
    }

    /** Test is round over when three players and only one responder passed then not over. */
    @Test
    void testIsRoundOver_whenThreePlayersAndOnlyOneResponderPassed_thenNotOver() {
        var round = this.roundService.registerPlay(RoomTestFactory.emptyRound(), this.makePair(7), "player-a");
        round = this.roundService.registerPass(round, "player-b");

        assertFalse(this.playRuleService.isRoundOver(round, List.of("player-a", "player-b", "player-c")));
    }

    /** Test is round over when three players and last player passed among others then still needs other pass. */
    @Test
    void testIsRoundOver_whenThreePlayersAndLastPlayerPassedAmongOthers_thenStillNeedsOtherPass() {
        var round = this.roundService.registerPlay(RoomTestFactory.emptyRound(), this.makePair(7), "player-a");
        round = this.roundService.registerPass(round, "player-a");
        round = this.roundService.registerPass(round, "player-b");

        assertFalse(this.playRuleService.isRoundOver(round, List.of("player-a", "player-b", "player-c")));
    }

    /** Test is round over when plin in two players and opponent skipped then over immediately. */
    @Test
    void testIsRoundOver_whenPlinInTwoPlayersAndOpponentSkipped_thenOverImmediately() {
        var round = this.roundService.registerPlay(RoomTestFactory.emptyRound(), this.makePair(7), "player-a");
        round = this.roundService.registerPlinPlay(round, this.makePair(7), "player-b", "player-a");

        assertTrue(this.playRuleService.isRoundOver(round, List.of("player-a", "player-b")));
    }

    /** Test is round over when plin in three players and responder passed then over. */
    @Test
    void testIsRoundOver_whenPlinInThreePlayersAndResponderPassed_thenOver() {
        var round = this.roundService.registerPlay(RoomTestFactory.emptyRound(), this.makePair(7), "player-a");
        round = this.roundService.registerPlinPlay(round, this.makePair(7), "player-b", "player-c");
        round = this.roundService.registerPass(round, "player-a");

        assertTrue(this.playRuleService.isRoundOver(round, List.of("player-a", "player-b", "player-c")));
    }

    /** Test is round over when plin in three players and only skipped player passed then not over. */
    @Test
    void testIsRoundOver_whenPlinInThreePlayersAndOnlySkippedPlayerPassed_thenNotOver() {
        var round = this.roundService.registerPlay(RoomTestFactory.emptyRound(), this.makePair(7), "player-a");
        round = this.roundService.registerPlinPlay(round, this.makePair(7), "player-b", "player-c");
        round = this.roundService.registerPass(round, "player-c");

        assertFalse(this.playRuleService.isRoundOver(round, List.of("player-a", "player-b", "player-c")));
    }

    /**
     * Closed round with pair.
     *
     * @param number the number
     * @return the round
     */
    private Round closedRoundWithPair(final Integer number) {
        return this.roundService.registerPlay(RoomTestFactory.emptyRound(), this.makePair(number), "player-a");
    }

    /**
     * Make pair.
     *
     * @param number the number
     * @return the play
     */
    private Play makePair(final Integer number) {
        return this.playBuilderService.buildPlay(List.of(
                this.cardFactoryService.buildCard(Suit.COPAS, number),
                this.cardFactoryService.buildCard(Suit.ESPADAS, number)));
    }
}
