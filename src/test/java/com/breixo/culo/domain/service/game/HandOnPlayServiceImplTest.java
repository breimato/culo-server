package com.breixo.culo.domain.service.game;

import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.cards.QuadDiscardApplied;
import com.breixo.culo.domain.model.cards.enums.Suit;
import com.breixo.culo.domain.model.game.Play;
import com.breixo.culo.domain.model.game.Round;
import com.breixo.culo.domain.model.room.GameSession;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.cards.QuadDiscardService;
import com.breixo.culo.domain.port.input.game.PlayRuleService;
import com.breixo.culo.domain.port.input.game.RoundService;
import com.breixo.culo.domain.port.input.game.TurnManagementService;
import com.breixo.culo.domain.port.input.player.HandManagementService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** The Class Hand On Play Service Impl Test. */
@ExtendWith(MockitoExtension.class)
class HandOnPlayServiceImplTest {

    /** The hand on play service. */
    @InjectMocks
    HandOnPlayServiceImpl handOnPlayService;

    /** The quad discard service. */
    @Mock
    QuadDiscardService quadDiscardService;

    /** The hand management service. */
    @Mock
    HandManagementService handManagementService;

    /** The play rule service. */
    @Mock
    PlayRuleService playRuleService;

    /** The round service. */
    @Mock
    RoundService roundService;

    /** The turn management service. */
    @Mock
    TurnManagementService turnManagementService;

    /** Test apply when normal play then register play. */
    @Test
    void testApply_whenNormalPlay_thenRegisterPlay() {
        // Given
        final var currentRound = Instancio.create(Round.class);
        final var updatedRound = Instancio.create(Round.class);
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::currentRound), currentRound)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::gameSession), gameSession)
                .create();
        final var player = Instancio.create(Player.class);
        final var playCard = Card.builder().suit(Suit.COPAS).number(3).build();
        final var play = Play.builder().cards(List.of(playCard)).build();
        final var roomAfterQuads = room;
        final var roomAfterRemove = Instancio.create(Room.class);
        final var roomAfterSecondQuads = roomAfterRemove.toBuilder()
                .gameSession(roomAfterRemove.gameSession().toBuilder()
                        .currentRound(currentRound)
                        .build())
                .build();
        final var initialQuadDiscardApplied = QuadDiscardApplied.builder()
                .room(roomAfterQuads)
                .events(List.of())
                .build();
        final var secondQuadDiscardApplied = QuadDiscardApplied.builder()
                .room(roomAfterSecondQuads)
                .events(List.of())
                .build();

        // When
        when(this.quadDiscardService.discardQuads(room, player.id())).thenReturn(initialQuadDiscardApplied);
        when(this.playRuleService.isPlin(play, currentRound)).thenReturn(false);
        when(this.playRuleService.isAsOros(play)).thenReturn(false);
        when(this.handManagementService.removeCardsFromHand(roomAfterQuads, player.id(), play.cards()))
                .thenReturn(roomAfterRemove);
        when(this.quadDiscardService.discardQuads(roomAfterRemove, player.id())).thenReturn(secondQuadDiscardApplied);
        when(this.roundService.registerPlay(currentRound, play, player.id())).thenReturn(updatedRound);
        final var handAfterPlay = this.handOnPlayService.apply(room, player, play);

        // Then
        verify(this.quadDiscardService, times(1)).discardQuads(room, player.id());
        verify(this.playRuleService, times(1)).isPlin(play, currentRound);
        verify(this.playRuleService, times(1)).isAsOros(play);
        verify(this.handManagementService, times(1)).removeCardsFromHand(roomAfterQuads, player.id(), play.cards());
        verify(this.quadDiscardService, times(1)).discardQuads(roomAfterRemove, player.id());
        verify(this.roundService, times(1)).registerPlay(currentRound, play, player.id());
        assertEquals(updatedRound, handAfterPlay.room().gameSession().currentRound());
        assertFalse(handAfterPlay.playFlags().plin());
        assertFalse(handAfterPlay.playFlags().isAsOros());
    }

    /** Test apply when plin then register plin play. */
    @Test
    void testApply_whenPlin_thenRegisterPlinPlay() {
        // Given
        final var currentRound = Instancio.create(Round.class);
        final var updatedRound = Instancio.create(Round.class);
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::currentRound), currentRound)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::gameSession), gameSession)
                .create();
        final var player = Instancio.create(Player.class);
        final var play = Play.builder()
                .cards(List.of(Card.builder().suit(Suit.OROS).number(7).build()))
                .build();
        final var skippedPlayerId = "skipped-player";
        final var roomAfterQuads = room;
        final var roomAfterRemove = Instancio.create(Room.class);
        final var roomAfterSecondQuads = roomAfterRemove.toBuilder()
                .gameSession(roomAfterRemove.gameSession().toBuilder()
                        .currentRound(currentRound)
                        .build())
                .build();
        final var initialQuadDiscardApplied = QuadDiscardApplied.builder()
                .room(roomAfterQuads)
                .events(List.of())
                .build();
        final var secondQuadDiscardApplied = QuadDiscardApplied.builder()
                .room(roomAfterSecondQuads)
                .events(List.of())
                .build();

        // When
        when(this.quadDiscardService.discardQuads(room, player.id())).thenReturn(initialQuadDiscardApplied);
        when(this.playRuleService.isPlin(play, currentRound)).thenReturn(true);
        when(this.playRuleService.isAsOros(play)).thenReturn(false);
        when(this.handManagementService.removeCardsFromHand(roomAfterQuads, player.id(), play.cards()))
                .thenReturn(roomAfterRemove);
        when(this.quadDiscardService.discardQuads(roomAfterRemove, player.id())).thenReturn(secondQuadDiscardApplied);
        when(this.turnManagementService.getNextActivePlayerId(roomAfterRemove)).thenReturn(skippedPlayerId);
        when(this.roundService.registerPlinPlay(currentRound, play, player.id(), skippedPlayerId))
                .thenReturn(updatedRound);
        final var handAfterPlay = this.handOnPlayService.apply(room, player, play);

        // Then
        verify(this.quadDiscardService, times(1)).discardQuads(room, player.id());
        verify(this.playRuleService, times(1)).isPlin(play, currentRound);
        verify(this.playRuleService, times(1)).isAsOros(play);
        verify(this.handManagementService, times(1)).removeCardsFromHand(roomAfterQuads, player.id(), play.cards());
        verify(this.quadDiscardService, times(1)).discardQuads(roomAfterRemove, player.id());
        verify(this.turnManagementService, times(1)).getNextActivePlayerId(roomAfterRemove);
        verify(this.roundService, times(1)).registerPlinPlay(currentRound, play, player.id(), skippedPlayerId);
        assertTrue(handAfterPlay.playFlags().plin());
    }

    /** Test apply when as oros then reset round. */
    @Test
    void testApply_whenAsOros_thenResetRound() {
        // Given
        final var currentRound = Instancio.create(Round.class);
        final var resetRound = Round.builder()
                .requirement(0)
                .lastCardNumber(0)
                .playersPassedSinceLastPlay(new HashSet<>())
                .lastPlayedCards(List.of())
                .build();
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::currentRound), currentRound)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::gameSession), gameSession)
                .create();
        final var player = Instancio.create(Player.class);
        final var play = Play.builder()
                .cards(List.of(Card.builder().suit(Suit.OROS).number(1).build()))
                .build();
        final var roomAfterQuads = room;
        final var roomAfterRemove = Instancio.create(Room.class);
        final var roomAfterSecondQuads = roomAfterRemove.toBuilder()
                .gameSession(roomAfterRemove.gameSession().toBuilder()
                        .currentRound(currentRound)
                        .build())
                .build();
        final var initialQuadDiscardApplied = QuadDiscardApplied.builder()
                .room(roomAfterQuads)
                .events(List.of())
                .build();
        final var secondQuadDiscardApplied = QuadDiscardApplied.builder()
                .room(roomAfterSecondQuads)
                .events(List.of())
                .build();

        // When
        when(this.quadDiscardService.discardQuads(room, player.id())).thenReturn(initialQuadDiscardApplied);
        when(this.playRuleService.isPlin(play, currentRound)).thenReturn(false);
        when(this.playRuleService.isAsOros(play)).thenReturn(true);
        when(this.handManagementService.removeCardsFromHand(roomAfterQuads, player.id(), play.cards()))
                .thenReturn(roomAfterRemove);
        when(this.quadDiscardService.discardQuads(roomAfterRemove, player.id())).thenReturn(secondQuadDiscardApplied);
        when(this.roundService.reset(currentRound)).thenReturn(resetRound);
        final var handAfterPlay = this.handOnPlayService.apply(room, player, play);

        // Then
        verify(this.quadDiscardService, times(1)).discardQuads(room, player.id());
        verify(this.playRuleService, times(1)).isPlin(play, currentRound);
        verify(this.playRuleService, times(1)).isAsOros(play);
        verify(this.handManagementService, times(1)).removeCardsFromHand(roomAfterQuads, player.id(), play.cards());
        verify(this.quadDiscardService, times(1)).discardQuads(roomAfterRemove, player.id());
        verify(this.roundService, times(1)).reset(currentRound);
        assertTrue(handAfterPlay.playFlags().isAsOros());
        assertEquals(resetRound, handAfterPlay.room().gameSession().currentRound());
    }
}
