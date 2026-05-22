package com.breixo.culo.domain.service.game;

import com.breixo.culo.domain.model.game.Round;
import com.breixo.culo.domain.model.room.GameSession;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.game.PlayRuleService;
import com.breixo.culo.domain.port.input.game.TurnManagementService;
import com.breixo.culo.domain.port.input.room.GameContextService;
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
 * The Class RoundClosingServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
class RoundClosingServiceImplTest {

    /** The round closing service. */
    @InjectMocks
    RoundClosingServiceImpl roundClosingService;

    /** The play rule service. */
    @Mock
    PlayRuleService playRuleService;

    /** The turn management service. */
    @Mock
    TurnManagementService turnManagementService;

    /** The game context service. */
    @Mock
    GameContextService gameContextService;

    /**
	 * Test close round if others all passed when round not over then round closed
	 * is false.
	 */
    @Test
    void testCloseRoundIfOthersAllPassed_whenRoundNotOver_thenRoundClosedIsFalse() {
        
        // Given
        final var currentRound = Instancio.create(Round.class);
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::currentRound), currentRound)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::gameSession), gameSession)
                .create();
        final var activePlayerIds = List.of("p1", "p2");

        // When
        when(this.gameContextService.activePlayerIds(room)).thenReturn(activePlayerIds);
        when(this.playRuleService.isRoundOver(currentRound, activePlayerIds)).thenReturn(false);
        final var roundClosure = this.roundClosingService.closeRoundIfOthersAllPassed(room);

        // Then
        verify(this.gameContextService, times(1)).activePlayerIds(room);
        verify(this.playRuleService, times(1)).isRoundOver(currentRound, activePlayerIds);
        assertEquals(room, roundClosure.room());
        assertFalse(roundClosure.roundClosed());
    }

    /**
	 * Test close round if others all passed when round over then finish round.
	 */
    @Test
    void testCloseRoundIfOthersAllPassed_whenRoundOver_thenFinishRound() {
        
        // Given
        final var currentRound = Instancio.create(Round.class);
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::currentRound), currentRound)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::gameSession), gameSession)
                .create();
        final var roomAfterClose = Instancio.create(Room.class);
        final var activePlayerIds = List.of("p1");

        // When
        when(this.gameContextService.activePlayerIds(room)).thenReturn(activePlayerIds);
        when(this.playRuleService.isRoundOver(currentRound, activePlayerIds)).thenReturn(true);
        when(this.turnManagementService.finishRoundAndSetOpener(room)).thenReturn(roomAfterClose);
        final var roundClosure = this.roundClosingService.closeRoundIfOthersAllPassed(room);

        // Then
        verify(this.gameContextService, times(1)).activePlayerIds(room);
        verify(this.playRuleService, times(1)).isRoundOver(currentRound, activePlayerIds);
        verify(this.turnManagementService, times(1)).finishRoundAndSetOpener(room);
        assertEquals(roomAfterClose, roundClosure.room());
        assertTrue(roundClosure.roundClosed());
    }
}
