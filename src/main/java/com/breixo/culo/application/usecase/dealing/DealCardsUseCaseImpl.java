package com.breixo.culo.application.usecase.dealing;

import com.breixo.culo.domain.command.game.DealCardsCommand;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.port.input.dealing.DealingCompletionService;
import com.breixo.culo.domain.port.input.dealing.DealingPolicyValidationService;
import com.breixo.culo.domain.port.input.game.DealCardsUseCase;
import com.breixo.culo.domain.port.input.session.GameSessionContextService;
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
    private final GameSessionContextService gameSessionContextService;

    /** The dealing policy validation service. */
    private final DealingPolicyValidationService dealingPolicyValidationService;

    /** The dealing completion service. */
    private final DealingCompletionService dealingCompletionService;

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
