package com.breixo.culo.application.usecase.game;

import com.breixo.culo.domain.command.game.PlayCardsCommand;
import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.game.Play;
import com.breixo.culo.domain.model.game.PlayExecutionResult;
import com.breixo.culo.domain.model.room.GameSessionContext;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.port.input.game.PlayBuilderService;
import com.breixo.culo.domain.port.input.game.PlayExecutionService;
import com.breixo.culo.domain.port.input.game.PlayPolicyService;
import com.breixo.culo.domain.port.input.room.GameContextService;
import com.breixo.culo.domain.port.output.room.RoomSavePersistencePort;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** The Class Play Cards Use Case Impl Test. */
@ExtendWith(MockitoExtension.class)
class PlayCardsUseCaseImplTest {

    /** The play cards use case. */
    @InjectMocks
    PlayCardsUseCaseImpl playCardsUseCaseImpl;

    /** The room save persistence port. */
    @Mock
    RoomSavePersistencePort roomSavePersistencePort;

    /** The game context service. */
    @Mock
    GameContextService gameContextService;

    /** The play policy service. */
    @Mock
    PlayPolicyService playPolicyService;

    /** The play builder service. */
    @Mock
    PlayBuilderService playBuilderService;

    /** The play execution service. */
    @Mock
    PlayExecutionService playExecutionService;

    /** Test execute when command is valid then return play result. */
    @Test
    void testExecute_whenCommandIsValid_thenReturnPlayResult() {
        // Given
        final var playCardsCommand = Instancio.create(PlayCardsCommand.class);
        final var gameSessionContext = Instancio.create(GameSessionContext.class);
        final var cards = Instancio.createList(Card.class);
        final var play = Instancio.create(Play.class);
        final var playExecutionResult = Instancio.create(PlayExecutionResult.class);
        final var savedRoom = Instancio.create(Room.class);

        // When
        when(this.gameContextService.loadWithPhase(
                playCardsCommand.roomCode(),
                playCardsCommand.clientId(),
                GamePhase.PLAYING)).thenReturn(gameSessionContext);
        when(this.playBuilderService.toCards(
                playCardsCommand.cards(),
                gameSessionContext.room(),
                gameSessionContext.player().id())).thenReturn(cards);
        when(this.playBuilderService.buildPlay(cards)).thenReturn(play);
        when(this.playExecutionService.execute(
                gameSessionContext.room(),
                gameSessionContext.player(),
                play)).thenReturn(playExecutionResult);
        when(this.roomSavePersistencePort.save(playExecutionResult.room())).thenReturn(savedRoom);
        doNothing().when(this.playPolicyService).validatePlayerCanPlay(gameSessionContext);
        doNothing().when(this.playPolicyService).validateLegalPlay(play, gameSessionContext.room());
        final var playResult = this.playCardsUseCaseImpl.execute(playCardsCommand);

        // Then
        verify(this.gameContextService, times(1)).loadWithPhase(
                playCardsCommand.roomCode(),
                playCardsCommand.clientId(),
                GamePhase.PLAYING);
        verify(this.playPolicyService, times(1)).validatePlayerCanPlay(gameSessionContext);
        verify(this.playBuilderService, times(1)).toCards(
                playCardsCommand.cards(),
                gameSessionContext.room(),
                gameSessionContext.player().id());
        verify(this.playBuilderService, times(1)).buildPlay(cards);
        verify(this.playPolicyService, times(1)).validateLegalPlay(play, gameSessionContext.room());
        verify(this.playExecutionService, times(1)).execute(
                gameSessionContext.room(),
                gameSessionContext.player(),
                play);
        verify(this.roomSavePersistencePort, times(1)).save(playExecutionResult.room());
        assertEquals(savedRoom, playResult.room());
        assertEquals(gameSessionContext.player().id(), playResult.playerId());
        assertEquals(play, playResult.play());
        assertEquals(playExecutionResult.plin(), playResult.plin());
        assertEquals(playExecutionResult.roundClosed(), playResult.roundClosed());
        assertEquals(playExecutionResult.gameFinished(), playResult.gameFinished());
    }

    /** Test execute when play is illegal then throw game exception. */
    @Test
    void testExecute_whenPlayIsIllegal_thenThrowGameException() {
        // Given
        final var playCardsCommand = Instancio.create(PlayCardsCommand.class);
        final var gameSessionContext = Instancio.create(GameSessionContext.class);
        final var cards = Instancio.createList(Card.class);
        final var play = Instancio.create(Play.class);

        // When
        when(this.gameContextService.loadWithPhase(
                playCardsCommand.roomCode(),
                playCardsCommand.clientId(),
                GamePhase.PLAYING)).thenReturn(gameSessionContext);
        when(this.playBuilderService.toCards(
                playCardsCommand.cards(),
                gameSessionContext.room(),
                gameSessionContext.player().id())).thenReturn(cards);
        when(this.playBuilderService.buildPlay(cards)).thenReturn(play);
        doNothing().when(this.playPolicyService).validatePlayerCanPlay(gameSessionContext);
        doThrow(new GameException(GameExceptionConstants.ILLEGAL_PLAY))
                .when(this.playPolicyService).validateLegalPlay(play, gameSessionContext.room());
        final var gameException = assertThrows(
                GameException.class,
                () -> this.playCardsUseCaseImpl.execute(playCardsCommand));

        // Then
        verify(this.gameContextService, times(1)).loadWithPhase(
                playCardsCommand.roomCode(),
                playCardsCommand.clientId(),
                GamePhase.PLAYING);
        verify(this.playPolicyService, times(1)).validatePlayerCanPlay(gameSessionContext);
        verify(this.playBuilderService, times(1)).toCards(
                playCardsCommand.cards(),
                gameSessionContext.room(),
                gameSessionContext.player().id());
        verify(this.playBuilderService, times(1)).buildPlay(cards);
        verify(this.playPolicyService, times(1)).validateLegalPlay(play, gameSessionContext.room());
        assertEquals(GameExceptionConstants.ILLEGAL_PLAY, gameException.getMessage());
    }
}
