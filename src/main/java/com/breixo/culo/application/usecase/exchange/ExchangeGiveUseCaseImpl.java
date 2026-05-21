package com.breixo.culo.application.usecase.exchange;

import com.breixo.culo.domain.command.game.ExchangeGiveCommand;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.port.input.exchange.ExchangePolicyValidationService;
import com.breixo.culo.domain.port.input.exchange.ExchangeService;
import com.breixo.culo.domain.port.input.game.ExchangeGiveUseCase;
import com.breixo.culo.domain.port.input.play.PlayBuilderService;
import com.breixo.culo.domain.port.input.session.GameSessionContextService;
import com.breixo.culo.domain.port.output.room.RoomSavePersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;

/** The Class ExchangeGiveUseCaseImpl. */
@Component
@RequiredArgsConstructor
public class ExchangeGiveUseCaseImpl implements ExchangeGiveUseCase {

    /** The room save persistence port. */
    private final RoomSavePersistencePort roomSavePersistencePort;

    /** The game session context service. */
    private final GameSessionContextService gameSessionContextService;

    /** The exchange policy validation service. */
    private final ExchangePolicyValidationService exchangePolicyValidationService;

    /** The play builder service. */
    private final PlayBuilderService playBuilderService;

    /** The exchange service. */
    private final ExchangeService exchangeService;

    /** {@inheritDoc} */
    @Override
    public Room execute(final ExchangeGiveCommand exchangeGiveCommand) {

        final var gameSessionContext = this.gameSessionContextService.loadWithPhase(
                exchangeGiveCommand.roomCode(),
                exchangeGiveCommand.clientId(),
                GamePhase.EXCHANGE);

        this.exchangePolicyValidationService.validateNotAlreadyDone(
                gameSessionContext.room(),
                gameSessionContext.player());
        this.exchangePolicyValidationService.validateRoleCanExchange(gameSessionContext.player());

        final var cards = this.playBuilderService.toCards(
                exchangeGiveCommand.cards(),
                gameSessionContext.room(),
                gameSessionContext.player().id());
        this.exchangePolicyValidationService.validateGiveCardsCount(gameSessionContext.player(), cards);

        final var roomAfterGive = this.exchangeService.processGive(
                gameSessionContext.room(),
                gameSessionContext.player().id(),
                exchangeGiveCommand);

        final var exchangeDone = new HashSet<>(roomAfterGive.exchangeState().exchangeDone());
        exchangeDone.add(gameSessionContext.player().id());
        final var roomWithExchangeDone = roomAfterGive.toBuilder()
                .exchangeState(roomAfterGive.exchangeState().toBuilder()
                        .exchangeDone(exchangeDone)
                        .build())
                .build();

        final var roomAfterFinalize = this.exchangeService.finalizeIfComplete(roomWithExchangeDone);

        return this.roomSavePersistencePort.save(roomAfterFinalize);
    }
}
