package com.breixo.culo.domain.service.game;

import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.model.game.Play;
import com.breixo.culo.domain.model.room.GameSessionContext;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.game.PlayRuleService;
import com.breixo.culo.domain.port.input.room.GameContextService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The Class PlayPolicyServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
class PlayPolicyServiceImplTest {

    /** The play policy service. */
    @InjectMocks
    PlayPolicyServiceImpl playPolicyService;

    /** The game context service. */
    @Mock
    GameContextService gameContextService;

    /** The play rule service. */
    @Mock
    PlayRuleService playRuleService;

    /**
	 * Test validate player can play when context is valid then no exception.
	 */
    @Test
    void testValidatePlayerCanPlay_whenContextIsValid_thenNoException() {
        
        // Given
        final var gameSessionContext = Instancio.create(GameSessionContext.class);

        // When
        doNothing().when(this.gameContextService)
                .requirePlayerTurn(gameSessionContext.room(), gameSessionContext.player());
        doNothing().when(this.gameContextService)
                .requirePlayerHasCards(gameSessionContext.room(), gameSessionContext.player());
        this.playPolicyService.validatePlayerCanPlay(gameSessionContext);

        // Then
        verify(this.gameContextService, times(1))
                .requirePlayerTurn(gameSessionContext.room(), gameSessionContext.player());
        verify(this.gameContextService, times(1))
                .requirePlayerHasCards(gameSessionContext.room(), gameSessionContext.player());
    }

    /**
	 * Test validate legal play when play is illegal then throw game exception.
	 */
    @Test
    void testValidateLegalPlay_whenPlayIsIllegal_thenThrowGameException() {
        
        // Given
        final var play = Instancio.create(Play.class);
        final var room = Instancio.create(Room.class);

        // When
        when(this.playRuleService.isLegal(play, room.gameSession().currentRound())).thenReturn(false);
        final var gameException = assertThrows(
                GameException.class,
                () -> this.playPolicyService.validateLegalPlay(play, room));

        // Then
        verify(this.playRuleService, times(1)).isLegal(play, room.gameSession().currentRound());
        assertEquals(GameExceptionConstants.ILLEGAL_PLAY, gameException.getMessage());
    }
}
