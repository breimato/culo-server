package com.breixo.culo.domain.service.game;

import com.breixo.culo.domain.model.cards.QuadDiscardApplied;
import com.breixo.culo.domain.model.game.Round;
import com.breixo.culo.domain.model.game.RoundClosure;
import com.breixo.culo.domain.model.room.GameSession;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.cards.QuadDiscardService;
import com.breixo.culo.domain.port.input.game.RoundClosingService;
import com.breixo.culo.domain.port.input.game.RoundService;
import com.breixo.culo.domain.port.input.game.TurnManagementService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The Class PassExecutionServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
class PassExecutionServiceImplTest {

    /** The pass execution service. */
    @InjectMocks
    PassExecutionServiceImpl passExecutionService;

    /** The quad discard service. */
    @Mock
    QuadDiscardService quadDiscardService;

    /** The round service. */
    @Mock
    RoundService roundService;

    /** The turn management service. */
    @Mock
    TurnManagementService turnManagementService;

    /** The round closing service. */
    @Mock
    RoundClosingService roundClosingService;

    /**
	 * Test execute when round closes immediately then round closed is true.
	 */
    @Test
    void testExecute_whenRoundClosesImmediately_thenRoundClosedIsTrue() {
        
        // Given
        final var player = Instancio.create(Player.class);
        final var currentRound = Instancio.create(Round.class);
        final var updatedRound = Instancio.create(Round.class);
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::currentRound), currentRound)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::gameSession), gameSession)
                .create();
        final var roomAfterQuads = room;
        final var roomAfterPass = roomAfterQuads.toBuilder()
                .gameSession(roomAfterQuads.gameSession().toBuilder()
                        .currentRound(updatedRound)
                        .build())
                .build();
        final var roomAfterClose = Instancio.create(Room.class);
        final var quadDiscardApplied = QuadDiscardApplied.builder()
                .room(roomAfterQuads)
                .events(List.of())
                .build();
        final var roundClosure = RoundClosure.builder()
                .room(roomAfterClose)
                .roundClosed(true)
                .build();

        // When
        when(this.quadDiscardService.discardQuads(room, player.id())).thenReturn(quadDiscardApplied);
        when(this.roundService.registerPass(currentRound, player.id())).thenReturn(updatedRound);
        when(this.roundClosingService.closeRoundIfOthersAllPassed(roomAfterPass)).thenReturn(roundClosure);
        final var passResult = this.passExecutionService.execute(room, player);

        // Then
        verify(this.quadDiscardService, times(1)).discardQuads(room, player.id());
        verify(this.roundService, times(1)).registerPass(currentRound, player.id());
        verify(this.roundClosingService, times(1)).closeRoundIfOthersAllPassed(roomAfterPass);
        assertEquals(roomAfterClose, passResult.room());
        assertEquals(player.id(), passResult.playerId());
        assertTrue(passResult.roundClosed());
    }

    /**
	 * Test execute when round not closed then advances turn and retries close.
	 */
    @Test
    void testExecute_whenRoundNotClosed_thenAdvancesTurnAndRetriesClose() {
        
        // Given
        final var player = Instancio.create(Player.class);
        final var currentRound = Instancio.create(Round.class);
        final var updatedRound = Instancio.create(Round.class);
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::currentRound), currentRound)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::gameSession), gameSession)
                .create();
        final var roomAfterQuads = room;
        final var roomAfterPass = roomAfterQuads.toBuilder()
                .gameSession(roomAfterQuads.gameSession().toBuilder()
                        .currentRound(updatedRound)
                        .build())
                .build();
        final var roomAfterAdvance = Instancio.create(Room.class);
        final var roomAfterSecondClose = Instancio.create(Room.class);
        final var quadDiscardApplied = QuadDiscardApplied.builder()
                .room(roomAfterQuads)
                .events(List.of())
                .build();
        final var firstRoundClosure = RoundClosure.builder()
                .room(roomAfterPass)
                .roundClosed(false)
                .build();
        final var secondRoundClosure = RoundClosure.builder()
                .room(roomAfterSecondClose)
                .roundClosed(true)
                .build();

        // When
        when(this.quadDiscardService.discardQuads(room, player.id())).thenReturn(quadDiscardApplied);
        when(this.roundService.registerPass(currentRound, player.id())).thenReturn(updatedRound);
        when(this.roundClosingService.closeRoundIfOthersAllPassed(roomAfterPass)).thenReturn(firstRoundClosure);
        when(this.turnManagementService.advanceTurn(roomAfterPass, false)).thenReturn(roomAfterAdvance);
        when(this.roundClosingService.closeRoundIfOthersAllPassed(roomAfterAdvance)).thenReturn(secondRoundClosure);
        final var passResult = this.passExecutionService.execute(room, player);

        // Then
        verify(this.quadDiscardService, times(1)).discardQuads(room, player.id());
        verify(this.roundService, times(1)).registerPass(currentRound, player.id());
        verify(this.roundClosingService, times(1)).closeRoundIfOthersAllPassed(roomAfterPass);
        verify(this.turnManagementService, times(1)).advanceTurn(roomAfterPass, false);
        verify(this.roundClosingService, times(1)).closeRoundIfOthersAllPassed(roomAfterAdvance);
        assertEquals(roomAfterSecondClose, passResult.room());
        assertTrue(passResult.roundClosed());
    }

    /**
	 * Test execute when advance turn and round still open then round closed is
	 * false.
	 */
    @Test
    void testExecute_whenAdvanceTurnAndRoundStillOpen_thenRoundClosedIsFalse() {
        
        // Given
        final var player = Instancio.create(Player.class);
        final var currentRound = Instancio.create(Round.class);
        final var updatedRound = Instancio.create(Round.class);
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::currentRound), currentRound)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::gameSession), gameSession)
                .create();
        final var roomAfterQuads = room;
        final var roomAfterPass = roomAfterQuads.toBuilder()
                .gameSession(roomAfterQuads.gameSession().toBuilder()
                        .currentRound(updatedRound)
                        .build())
                .build();
        final var roomAfterAdvance = Instancio.create(Room.class);
        final var quadDiscardApplied = QuadDiscardApplied.builder()
                .room(roomAfterQuads)
                .events(List.of())
                .build();
        final var openRoundClosure = RoundClosure.builder()
                .room(roomAfterPass)
                .roundClosed(false)
                .build();
        final var stillOpenRoundClosure = RoundClosure.builder()
                .room(roomAfterAdvance)
                .roundClosed(false)
                .build();

        // When
        when(this.quadDiscardService.discardQuads(room, player.id())).thenReturn(quadDiscardApplied);
        when(this.roundService.registerPass(currentRound, player.id())).thenReturn(updatedRound);
        when(this.roundClosingService.closeRoundIfOthersAllPassed(roomAfterPass)).thenReturn(openRoundClosure);
        when(this.turnManagementService.advanceTurn(roomAfterPass, false)).thenReturn(roomAfterAdvance);
        when(this.roundClosingService.closeRoundIfOthersAllPassed(roomAfterAdvance)).thenReturn(stillOpenRoundClosure);
        final var passResult = this.passExecutionService.execute(room, player);

        // Then
        verify(this.quadDiscardService, times(1)).discardQuads(room, player.id());
        verify(this.roundService, times(1)).registerPass(currentRound, player.id());
        verify(this.roundClosingService, times(1)).closeRoundIfOthersAllPassed(roomAfterPass);
        verify(this.turnManagementService, times(1)).advanceTurn(roomAfterPass, false);
        verify(this.roundClosingService, times(1)).closeRoundIfOthersAllPassed(roomAfterAdvance);
        assertEquals(roomAfterAdvance, passResult.room());
        assertFalse(passResult.roundClosed());
    }
}
