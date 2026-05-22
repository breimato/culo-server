package com.breixo.culo.application.usecase.room;

import com.breixo.culo.domain.command.room.CreateRoomCommand;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.RoomJoinResult;
import com.breixo.culo.domain.model.room.enums.PlayerRole;
import com.breixo.culo.domain.port.input.room.CreateRoomUseCase;
import com.breixo.culo.domain.port.input.room.RoomFactoryService;
import com.breixo.culo.domain.port.input.room.RoomMembershipService;
import com.breixo.culo.domain.port.output.room.RoomCodeGenerationPort;
import com.breixo.culo.domain.port.output.room.RoomSavePersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * The Class CreateRoomUseCaseImpl.
 */
@Component
@RequiredArgsConstructor
public class CreateRoomUseCaseImpl implements CreateRoomUseCase {

    /** The room save persistence port. */
    private final RoomSavePersistencePort roomSavePersistencePort;

    /** The room code generation port. */
    private final RoomCodeGenerationPort roomCodeGenerationPort;

    /** The room factory service. */
    private final RoomFactoryService roomFactoryService;

    /** The room membership service. */
    private final RoomMembershipService roomMembershipService;

    /** {@inheritDoc} */
    @Override
    public RoomJoinResult execute(final CreateRoomCommand createRoomCommand) {

        final var playerId = UUID.randomUUID().toString();
        final var player = Player.builder()
                .id(playerId)
                .clientId(createRoomCommand.clientId())
                .nick(createRoomCommand.nick())
                .connected(true)
                .role(PlayerRole.NONE)
                .build();
        final var roomCode = this.roomCodeGenerationPort.execute();
        final var room = this.roomFactoryService.createEmptyRoom(createRoomCommand, roomCode, playerId);
        final var roomWithPlayer = this.roomMembershipService.addPlayer(room, player);
        final var savedRoom = this.roomSavePersistencePort.save(roomWithPlayer);

        return RoomJoinResult.builder()
                .roomCode(roomCode)
                .playerId(playerId)
                .room(savedRoom)
                .build();
    }
}
