package com.breixo.culo.domain.service.room;

import com.breixo.culo.domain.constants.RoomConstants;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.room.RoomMembershipService;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;

/** The Class RoomMembershipServiceImpl. */
@Service
public class RoomMembershipServiceImpl implements RoomMembershipService {

    /** {@inheritDoc} */
    @Override
    public Room addPlayer(@NotNull final Room room, @NotNull final Player player) {

        if (Integer.valueOf(room.roomLobby().players().size()).compareTo(RoomConstants.MAX_PLAYERS) >= 0) {
            throw new RoomException(RoomExceptionConstants.ROOM_FULL);
        }

        final var players = new ArrayList<>(room.roomLobby().players());
        players.add(player);

        final var roomWithPlayer = room.toBuilder()
                .roomLobby(room.roomLobby().toBuilder()
                        .players(players)
                        .build())
                .build();

        return this.touch(roomWithPlayer);
    }

    /** {@inheritDoc} */
    @Override
    public Room reconnectPlayer(final Room room, final Player player) {

        final var updatedPlayers = room.roomLobby().players().stream()
                .map(playerInRoom -> playerInRoom.id().equals(player.id())
                        ? player.toBuilder().connected(true).build()
                        : playerInRoom)
                .toList();

        final var roomWithReconnectedPlayer = room.toBuilder()
                .roomLobby(room.roomLobby().toBuilder()
                        .players(updatedPlayers)
                        .build())
                .build();

        return this.touch(roomWithReconnectedPlayer);
    }

    /** {@inheritDoc} */
    @Override
    public Room touch(final Room room) {

        return room.toBuilder()
                .roomLobby(room.roomLobby().toBuilder()
                        .lastActivity(Instant.now())
                        .build())
                .build();
    }
}
