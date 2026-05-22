package com.breixo.culo.application.usecase.game;

import com.breixo.culo.domain.command.game.PassCommand;
import com.breixo.culo.domain.model.game.PassResult;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.port.input.game.PassUseCase;
import com.breixo.culo.domain.port.input.game.PassExecutionService;
import com.breixo.culo.domain.port.input.game.PlayPolicyService;
import com.breixo.culo.domain.port.input.room.GameContextService;
import com.breixo.culo.domain.port.output.room.RoomSavePersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** The Class PassUseCaseImpl. */
@Component
@RequiredArgsConstructor
public class PassUseCaseImpl implements PassUseCase {

    /** The room save persistence port. */
    private final RoomSavePersistencePort roomSavePersistencePort;

    /** The game session context service. */
    private final GameContextService gameSessionContextService;

    /** The play policy validation service. */
    private final PlayPolicyService playPolicyValidationService;

    /** The pass execution service. */
    private final PassExecutionService passExecutionService;

    /** {@inheritDoc} */
    @Override
    public PassResult execute(final PassCommand passCommand) {

        final var gameSessionContext = this.gameSessionContextService.loadWithPhase(
                passCommand.roomCode(),
                passCommand.clientId(),
                GamePhase.PLAYING);

        this.playPolicyValidationService.validatePlayerCanPlay(gameSessionContext);

        final var passResult = this.passExecutionService.execute(
                gameSessionContext.room(),
                gameSessionContext.player());

        final var savedRoom = this.roomSavePersistencePort.save(passResult.room());

        return PassResult.builder()
                .room(savedRoom)
                .playerId(passResult.playerId())
                .roundClosed(passResult.roundClosed())
                .build();
    }
}
