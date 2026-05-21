package com.breixo.culo.application.usecase.culoswap;

import com.breixo.culo.domain.command.game.CuloSwapInitiateCommand;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.port.input.culoswap.CuloSwapPolicyValidationService;
import com.breixo.culo.domain.port.input.culoswap.CuloSwapService;
import com.breixo.culo.domain.port.input.game.CuloSwapInitiateUseCase;
import com.breixo.culo.domain.port.input.room.RoomPhaseService;
import com.breixo.culo.domain.port.input.session.GameSessionContextService;
import com.breixo.culo.domain.port.output.room.RoomSavePersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** The Class CuloSwapInitiateUseCaseImpl. */
@Component
@RequiredArgsConstructor
public class CuloSwapInitiateUseCaseImpl implements CuloSwapInitiateUseCase {

    /** The room save persistence port. */
    private final RoomSavePersistencePort roomSavePersistencePort;

    /** The game session context service. */
    private final GameSessionContextService gameSessionContextService;

    /** The culo swap policy validation service. */
    private final CuloSwapPolicyValidationService culoSwapPolicyValidationService;

    /** The culo swap service. */
    private final CuloSwapService culoSwapService;

    /** The room phase service. */
    private final RoomPhaseService roomPhaseService;

    /** {@inheritDoc} */
    @Override
    public Room execute(final CuloSwapInitiateCommand culoSwapInitiateCommand) {

        final var gameSessionContext = this.gameSessionContextService.loadWithPhase(
                culoSwapInitiateCommand.roomCode(),
                culoSwapInitiateCommand.clientId(),
                GamePhase.DEALING);

        this.culoSwapPolicyValidationService.validateInitiator(gameSessionContext.player());
        this.culoSwapPolicyValidationService.validateNoActiveSwap(gameSessionContext.room());
        this.culoSwapPolicyValidationService.validateTargetExists(
                gameSessionContext.room(),
                culoSwapInitiateCommand.targetPlayerId());

        final var roomAfterInitiate = this.culoSwapService.initiateSwap(
                gameSessionContext.room(),
                gameSessionContext.player().id(),
                culoSwapInitiateCommand.targetPlayerId());
        final var roomWithPhase = this.roomPhaseService.withPhase(roomAfterInitiate, GamePhase.CULO_SWAP_VOTE);
        final var voteRegistrationResult = this.culoSwapService.registerCuloSwapVote(
                roomWithPhase,
                gameSessionContext.player().id(),
                true);

        return this.roomSavePersistencePort.save(voteRegistrationResult.room());
    }
}
