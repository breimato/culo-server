package com.breixo.culo.application.usecase.room;

import com.breixo.culo.domain.command.room.CloseRoomCommand;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.room.CloseRoomUseCase;
import com.breixo.culo.domain.port.input.room.GameContextService;
import com.breixo.culo.domain.port.output.room.RoomDeletionPersistencePort;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

/**
 * The Class CloseRoomUseCaseImpl.
 */
@Component
@RequiredArgsConstructor
public class CloseRoomUseCaseImpl implements CloseRoomUseCase {

    /** The game context service. */
    private final GameContextService gameContextService;

    /** The room deletion persistence port. */
    private final RoomDeletionPersistencePort roomDeletionPersistencePort;

    /** {@inheritDoc} */
    @Override
    public Room execute(final CloseRoomCommand closeRoomCommand) {

        final var gameSessionContext = this.gameContextService.load(
                closeRoomCommand.roomCode(),
                closeRoomCommand.clientId());

        if (BooleanUtils.isFalse(
                gameSessionContext.room().roomLobby().hostPlayerId().equals(gameSessionContext.player().id()))) {
            throw new RoomException(RoomExceptionConstants.NOT_HOST);
        }

        final var roomSnapshot = gameSessionContext.room();
        this.roomDeletionPersistencePort.deleteByCode(roomSnapshot.roomLobby().code());

        return roomSnapshot;
    }
}
