package com.breixo.culo.application.usecase.cards;

import com.breixo.culo.domain.command.cards.DealCardsCommand;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.port.input.cards.DealCompletionService;
import com.breixo.culo.domain.port.input.cards.DealPolicyService;
import com.breixo.culo.domain.port.input.cards.DealCardsUseCase;
import com.breixo.culo.domain.port.input.room.GameContextService;
import com.breixo.culo.domain.port.output.room.RoomSavePersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** The Class DealCardsUseCaseImpl. */
@Component
@RequiredArgsConstructor
public class DealCardsUseCaseImpl implements DealCardsUseCase {

    /** The room save persistence port. */
    private final RoomSavePersistencePort roomSavePersistencePort;

    /** The game session context service. */
    private final GameContextService gameSessionContextService;

    /** The dealing policy validation service. */
    private final DealPolicyService dealingPolicyValidationService;

    /** The dealing completion service. */
    private final DealCompletionService dealingCompletionService;

    /** {@inheritDoc} */
    @Override
    public Room execute(final DealCardsCommand dealCardsCommand) {

        final var gameSessionContext = this.gameSessionContextService.loadWithPhase(
                dealCardsCommand.roomCode(),
                dealCardsCommand.clientId(),
                GamePhase.DEALING);

        this.dealingPolicyValidationService.validateDealingAuthority(
                gameSessionContext.room(),
                gameSessionContext.player());

        final var roomAfterDealing = this.dealingCompletionService.execute(gameSessionContext.room());

        return this.roomSavePersistencePort.save(roomAfterDealing);
    }
}
