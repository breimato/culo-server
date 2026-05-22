package com.breixo.culo.application.usecase.swap;

import com.breixo.culo.domain.command.swap.ExchangeGiveCommand;
import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.room.ExchangeState;
import com.breixo.culo.domain.model.room.GameSessionContext;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.port.input.game.PlayBuilderService;
import com.breixo.culo.domain.port.input.room.GameContextService;
import com.breixo.culo.domain.port.input.swap.ExchangePolicyService;
import com.breixo.culo.domain.port.input.swap.ExchangeService;
import com.breixo.culo.domain.port.output.room.RoomSavePersistencePort;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The Class ExchangeGiveUseCaseImplTest.
 */
@ExtendWith(MockitoExtension.class)
class ExchangeGiveUseCaseImplTest {

    /** The exchange give use case impl. */
    @InjectMocks
    ExchangeGiveUseCaseImpl exchangeGiveUseCaseImpl;

    /** The room save persistence port. */
    @Mock
    RoomSavePersistencePort roomSavePersistencePort;

    /** The game context service. */
    @Mock
    GameContextService gameContextService;

    /** The exchange policy service. */
    @Mock
    ExchangePolicyService exchangePolicyService;

    /** The play builder service. */
    @Mock
    PlayBuilderService playBuilderService;

    /** The exchange service. */
    @Mock
    ExchangeService exchangeService;

    /**
	 * Test execute when command is valid then save and return room.
	 */
    @Test
    void testExecute_whenCommandIsValid_thenSaveAndReturnRoom() {
        
        // Given
        final var exchangeGiveCommand = Instancio.create(ExchangeGiveCommand.class);
        final var gameSessionContext = Instancio.create(GameSessionContext.class);
        final var cards = Instancio.createList(Card.class);
        final var roomAfterGive = Instancio.of(Room.class)
                .set(field(Room::exchangeState), Instancio.of(ExchangeState.class)
                        .set(field(ExchangeState::exchangeDone), new HashSet<String>())
                        .create())
                .create();
        final var roomAfterFinalize = Instancio.create(Room.class);
        final var savedRoom = Instancio.create(Room.class);
        final ArgumentCaptor<Room> roomArgumentCaptor = ArgumentCaptor.forClass(Room.class);

        // When
        when(this.gameContextService.loadWithPhase(
                exchangeGiveCommand.roomCode(),
                exchangeGiveCommand.clientId(),
                GamePhase.EXCHANGE)).thenReturn(gameSessionContext);
        when(this.playBuilderService.toCards(
                exchangeGiveCommand.cards(),
                gameSessionContext.room(),
                gameSessionContext.player().id())).thenReturn(cards);
        when(this.exchangeService.processGive(
                gameSessionContext.room(),
                gameSessionContext.player().id(),
                exchangeGiveCommand)).thenReturn(roomAfterGive);
        when(this.exchangeService.finalizeIfComplete(roomArgumentCaptor.capture())).thenReturn(roomAfterFinalize);
        when(this.roomSavePersistencePort.save(roomAfterFinalize)).thenReturn(savedRoom);
        doNothing().when(this.exchangePolicyService)
                .validateNotAlreadyDone(gameSessionContext.room(), gameSessionContext.player());
        doNothing().when(this.exchangePolicyService).validateRoleCanExchange(gameSessionContext.player());
        doNothing().when(this.exchangePolicyService).validateGiveCardsCount(gameSessionContext.player(), cards);
        final var room = this.exchangeGiveUseCaseImpl.execute(exchangeGiveCommand);

        // Then
        verify(this.gameContextService, times(1)).loadWithPhase(
                exchangeGiveCommand.roomCode(),
                exchangeGiveCommand.clientId(),
                GamePhase.EXCHANGE);
        verify(this.exchangePolicyService, times(1))
                .validateNotAlreadyDone(gameSessionContext.room(), gameSessionContext.player());
        verify(this.exchangePolicyService, times(1))
                .validateRoleCanExchange(gameSessionContext.player());
        verify(this.playBuilderService, times(1)).toCards(
                exchangeGiveCommand.cards(),
                gameSessionContext.room(),
                gameSessionContext.player().id());
        verify(this.exchangePolicyService, times(1))
                .validateGiveCardsCount(gameSessionContext.player(), cards);
        verify(this.exchangeService, times(1)).processGive(
                gameSessionContext.room(),
                gameSessionContext.player().id(),
                exchangeGiveCommand);
        verify(this.exchangeService, times(1)).finalizeIfComplete(roomArgumentCaptor.getValue());
        verify(this.roomSavePersistencePort, times(1)).save(roomAfterFinalize);
        assertEquals(savedRoom, room);
        assertTrue(roomArgumentCaptor.getValue().exchangeState().exchangeDone()
                .contains(gameSessionContext.player().id()));
    }

    /**
	 * Test execute when exchange already done then throw game exception.
	 */
    @Test
    void testExecute_whenExchangeAlreadyDone_thenThrowGameException() {
        
        // Given
        final var exchangeGiveCommand = Instancio.create(ExchangeGiveCommand.class);
        final var gameSessionContext = Instancio.create(GameSessionContext.class);

        // When
        when(this.gameContextService.loadWithPhase(
                exchangeGiveCommand.roomCode(),
                exchangeGiveCommand.clientId(),
                GamePhase.EXCHANGE)).thenReturn(gameSessionContext);
        doThrow(new GameException(GameExceptionConstants.EXCHANGE_ALREADY_DONE))
                .when(this.exchangePolicyService)
                .validateNotAlreadyDone(gameSessionContext.room(), gameSessionContext.player());
        final var gameException = assertThrows(
                GameException.class,
                () -> this.exchangeGiveUseCaseImpl.execute(exchangeGiveCommand));

        // Then
        verify(this.gameContextService, times(1)).loadWithPhase(
                exchangeGiveCommand.roomCode(),
                exchangeGiveCommand.clientId(),
                GamePhase.EXCHANGE);
        verify(this.exchangePolicyService, times(1))
                .validateNotAlreadyDone(gameSessionContext.room(), gameSessionContext.player());
        assertEquals(GameExceptionConstants.EXCHANGE_ALREADY_DONE, gameException.getMessage());
    }
}
