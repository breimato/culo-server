package com.breixo.culo.application.usecase.room;

import com.breixo.culo.domain.command.room.CreateRoomCommand;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.output.room.RoomCodeGenerationPort;
import com.breixo.culo.domain.port.output.room.RoomSavePersistencePort;
import com.breixo.culo.domain.service.room.RoomFactoryServiceImpl;
import com.breixo.culo.domain.service.room.RoomMembershipServiceImpl;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The Class CreateRoomUseCaseImplTest.
 */
@ExtendWith(MockitoExtension.class)
class CreateRoomUseCaseImplTest {

    /** The room save persistence port. */
    @Mock
    RoomSavePersistencePort roomSavePersistencePort;

    /** The room code generation port. */
    @Mock
    RoomCodeGenerationPort roomCodeGenerationPort;

    /** The room factory service. */
    final RoomFactoryServiceImpl roomFactoryService = new RoomFactoryServiceImpl();

    /** The room membership service. */
    final RoomMembershipServiceImpl roomMembershipService = new RoomMembershipServiceImpl();

    /** The create room use case impl. */
    CreateRoomUseCaseImpl createRoomUseCaseImpl;

    /**
	 * Sets the up.
	 */
    @BeforeEach
    void setUp() {
        this.createRoomUseCaseImpl = new CreateRoomUseCaseImpl(
                this.roomSavePersistencePort,
                this.roomCodeGenerationPort,
                this.roomFactoryService,
                this.roomMembershipService);
    }

    /**
	 * Test execute when command is valid then persist and return join result.
	 */
    @Test
    void testExecute_whenCommandIsValid_thenPersistAndReturnJoinResult() {

        // Given
        final var createRoomCommand = Instancio.create(CreateRoomCommand.class);
        final var roomCode = Instancio.create(String.class);
        final ArgumentCaptor<Room> roomArgumentCaptor = ArgumentCaptor.forClass(Room.class);

        // When
        when(this.roomCodeGenerationPort.execute()).thenReturn(roomCode);
        when(this.roomSavePersistencePort.save(roomArgumentCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        final var roomJoinResult = this.createRoomUseCaseImpl.execute(createRoomCommand);

        // Then
        verify(this.roomCodeGenerationPort, times(1)).execute();
        verify(this.roomSavePersistencePort, times(1)).save(roomArgumentCaptor.getValue());
        assertEquals(roomCode, roomJoinResult.roomCode());
        assertNotNull(roomJoinResult.playerId());
        assertEquals(createRoomCommand.clientId(), roomArgumentCaptor.getValue().roomLobby().players().getFirst().clientId());
        assertEquals(roomArgumentCaptor.getValue(), roomJoinResult.room());
    }
}
