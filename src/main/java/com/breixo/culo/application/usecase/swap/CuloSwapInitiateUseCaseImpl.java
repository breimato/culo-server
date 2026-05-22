package com.breixo.culo.application.usecase.swap;

import com.breixo.culo.domain.command.swap.CuloSwapInitiateCommand;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.port.input.swap.CuloSwapPolicyService;
import com.breixo.culo.domain.port.input.swap.CuloService;
import com.breixo.culo.domain.port.input.swap.CuloSwapInitiateUseCase;
import com.breixo.culo.domain.port.input.room.RoomPhaseService;
import com.breixo.culo.domain.port.input.room.GameContextService;
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
    private final GameContextService gameSessionContextService;

    /** The culo policy validation service. */
    private final CuloSwapPolicyService culoPolicyValidationService;

    /** The culo service. */
    private final CuloService culoService;

    /** The room phase service. */
    private final RoomPhaseService roomPhaseService;

    /** {@inheritDoc} */
    @Override
    public Room execute(final CuloSwapInitiateCommand culoSwapInitiateCommand) {

        final var gameSessionContext = this.gameSessionContextService.loadWithPhase(
                culoSwapInitiateCommand.roomCode(),
                culoSwapInitiateCommand.clientId(),
                GamePhase.DEALING);

        this.culoPolicyValidationService.validateInitiator(gameSessionContext.player());
        this.culoPolicyValidationService.validateNoActiveSwap(gameSessionContext.room());
        this.culoPolicyValidationService.validateTargetExists(
                gameSessionContext.room(),
                culoSwapInitiateCommand.targetPlayerId());

        final var roomAfterInitiate = this.culoService.initiateSwap(
                gameSessionContext.room(),
                gameSessionContext.player().id(),
                culoSwapInitiateCommand.targetPlayerId());
        final var roomWithPhase = this.roomPhaseService.withPhase(roomAfterInitiate, GamePhase.CULO_SWAP_VOTE);
        final var voteCast = this.culoService.registerVote(
                roomWithPhase,
                gameSessionContext.player().id(),
                true);

        return this.roomSavePersistencePort.save(voteCast.room());
    }
}
