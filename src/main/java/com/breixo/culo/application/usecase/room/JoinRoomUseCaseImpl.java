package com.breixo.culo.application.usecase.room;

import com.breixo.culo.domain.command.room.JoinRoomCommand;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.RoomJoinResult;
import com.breixo.culo.domain.model.room.enums.PlayerRole;
import com.breixo.culo.domain.port.input.room.JoinRoomUseCase;
import com.breixo.culo.domain.port.input.room.PlayerLookupService;
import com.breixo.culo.domain.port.input.room.RoomMembershipService;
import com.breixo.culo.domain.port.input.room.RoomPhaseService;
import com.breixo.culo.domain.port.output.room.RoomRetrievalPersistencePort;
import com.breixo.culo.domain.port.output.room.RoomSavePersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** The Class JoinRoomUseCaseImpl. */
@Component
@RequiredArgsConstructor
public class JoinRoomUseCaseImpl implements JoinRoomUseCase {

    /** The room save persistence port. */
    private final RoomSavePersistencePort roomSavePersistencePort;

    /** The room retrieval persistence port. */
    private final RoomRetrievalPersistencePort roomRetrievalPersistencePort;

    /** The player lookup service. */
    private final PlayerLookupService playerLookupService;

    /** The room phase service. */
    private final RoomPhaseService roomPhaseService;

    /** The room membership service. */
    private final RoomMembershipService roomMembershipService;

    /** {@inheritDoc} */
    @Override
    public RoomJoinResult execute(final JoinRoomCommand joinRoomCommand) {

        final var room = this.roomRetrievalPersistencePort.findByCode(joinRoomCommand.roomCode())
                .orElseThrow(() -> new RoomException(RoomExceptionConstants.ROOM_NOT_FOUND));
        final var existingPlayer = this.playerLookupService.findPlayerByClientId(room, joinRoomCommand.clientId());

        if (existingPlayer.isPresent()) {
            return this.reconnect(room, existingPlayer.get());
        }

        this.roomPhaseService.requireLobbyPhase(room);

        final var playerId = UUID.randomUUID().toString();
        final var player = Player.builder()
                .id(playerId)
                .clientId(joinRoomCommand.clientId())
                .nick(joinRoomCommand.nick())
                .connected(true)
                .role(PlayerRole.NONE)
                .build();
        final var roomWithPlayer = this.roomMembershipService.addPlayer(room, player);
        final var savedRoom = this.roomSavePersistencePort.save(roomWithPlayer);

        return RoomJoinResult.builder()
                .roomCode(savedRoom.roomLobby().code())
                .playerId(playerId)
                .room(savedRoom)
                .build();
    }

    /**
     * Reconnect.
     *
     * @param room   the room
     * @param player the player
     * @return the room join result
     */
    private RoomJoinResult reconnect(final Room room, final Player player) {

        final var roomWithReconnect = this.roomMembershipService.reconnectPlayer(room, player);
        final var savedRoom = this.roomSavePersistencePort.save(roomWithReconnect);

        return RoomJoinResult.builder()
                .roomCode(savedRoom.roomLobby().code())
                .playerId(player.id())
                .room(savedRoom)
                .build();
    }
}
