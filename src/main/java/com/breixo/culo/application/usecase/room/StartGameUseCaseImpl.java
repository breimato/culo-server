package com.breixo.culo.application.usecase.room;

import com.breixo.culo.domain.command.room.StartGameCommand;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.port.input.room.RoomPhaseService;
import com.breixo.culo.domain.port.input.room.RoomStartPolicyValidationService;
import com.breixo.culo.domain.port.input.room.StartGameUseCase;
import com.breixo.culo.domain.port.input.session.GameSessionContextService;
import com.breixo.culo.domain.port.output.room.RoomSavePersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** The Class StartGameUseCaseImpl. */
@Component
@RequiredArgsConstructor
public class StartGameUseCaseImpl implements StartGameUseCase {

    /** The room save persistence port. */
    private final RoomSavePersistencePort roomSavePersistencePort;

    /** The game session context service. */
    private final GameSessionContextService gameSessionContextService;

    /** The room phase service. */
    private final RoomPhaseService roomPhaseService;

    /** The room start policy validation service. */
    private final RoomStartPolicyValidationService roomStartPolicyValidationService;

    /** {@inheritDoc} */
    @Override
    public Room execute(final StartGameCommand startGameCommand) {

        final var gameSessionContext = this.gameSessionContextService.load(
                startGameCommand.roomCode(),
                startGameCommand.clientId());

        this.roomPhaseService.requireLobbyPhase(gameSessionContext.room());
        this.roomStartPolicyValidationService.validateCanStart(
                gameSessionContext.room(),
                gameSessionContext.player());

        final var roomWithPhase = this.roomPhaseService.withPhase(gameSessionContext.room(), GamePhase.DEALING);

        return this.roomSavePersistencePort.save(roomWithPhase);
    }
}
