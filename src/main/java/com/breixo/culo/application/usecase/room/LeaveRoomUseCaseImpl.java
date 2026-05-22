package com.breixo.culo.application.usecase.room;

import com.breixo.culo.domain.command.room.LeaveRoomCommand;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.room.GameContextService;
import com.breixo.culo.domain.port.input.room.LeaveRoomUseCase;
import com.breixo.culo.domain.port.input.room.PlayerRemovalFromRoomService;
import com.breixo.culo.domain.port.input.room.RoomMembershipService;
import com.breixo.culo.domain.port.output.room.RoomDeletionPersistencePort;
import com.breixo.culo.domain.port.output.room.RoomSavePersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * The Class LeaveRoomUseCaseImpl.
 */
@Component
@RequiredArgsConstructor
public class LeaveRoomUseCaseImpl implements LeaveRoomUseCase {

    /** The game context service. */
    private final GameContextService gameContextService;

    /** The room membership service. */
    private final RoomMembershipService roomMembershipService;

    /** The player removal from room service. */
    private final PlayerRemovalFromRoomService playerRemovalFromRoomService;

    /** The room save persistence port. */
    private final RoomSavePersistencePort roomSavePersistencePort;

    /** The room deletion persistence port. */
    private final RoomDeletionPersistencePort roomDeletionPersistencePort;

    /** {@inheritDoc} */
    @Override
    public Optional<Room> execute(final LeaveRoomCommand leaveRoomCommand) {

        final var gameSessionContext = this.gameContextService.load(
                leaveRoomCommand.roomCode(),
                leaveRoomCommand.clientId());

        final var roomWithoutLobbyPlayer = this.roomMembershipService.removePlayer(
                gameSessionContext.room(),
                gameSessionContext.player());

        if (roomWithoutLobbyPlayer.roomLobby().players().isEmpty()) {
            this.roomDeletionPersistencePort.deleteByCode(roomWithoutLobbyPlayer.roomLobby().code());
            return Optional.empty();
        }

        final var roomWithoutGamePlayer = this.playerRemovalFromRoomService.removePlayerFromGameState(
                roomWithoutLobbyPlayer,
                gameSessionContext.player().id());

        return Optional.of(this.roomSavePersistencePort.save(roomWithoutGamePlayer));
    }
}
