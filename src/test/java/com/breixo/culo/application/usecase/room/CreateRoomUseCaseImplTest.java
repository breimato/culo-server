package com.breixo.culo.application.usecase.room;

import com.breixo.culo.domain.command.room.CreateRoomCommand;
import com.breixo.culo.domain.port.output.room.RoomCodeGenerationPort;
import com.breixo.culo.domain.service.room.RoomFactoryServiceImpl;
import com.breixo.culo.domain.service.room.RoomMembershipServiceImpl;
import com.breixo.culo.testsupport.InMemoryRoomStore;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** The Class Create Room Use Case Impl Test. */
@ExtendWith(MockitoExtension.class)
class CreateRoomUseCaseImplTest {

    @Mock
    RoomCodeGenerationPort roomCodeGenerationPort;

    final InMemoryRoomStore inMemoryRoomStore = new InMemoryRoomStore();

    final RoomFactoryServiceImpl roomFactoryService = new RoomFactoryServiceImpl();

    final RoomMembershipServiceImpl roomMembershipService = new RoomMembershipServiceImpl();

    CreateRoomUseCaseImpl createRoomUseCaseImpl;

    @BeforeEach
    void setUp() {
        this.createRoomUseCaseImpl = new CreateRoomUseCaseImpl(
                this.inMemoryRoomStore,
                this.roomCodeGenerationPort,
                this.roomFactoryService,
                this.roomMembershipService);
    }

    /** Test execute when command is valid then persist and return join result. */
    @Test
    void testExecute_whenCommandIsValid_thenPersistAndReturnJoinResult() {
        // Given
        final var createRoomCommand = Instancio.create(CreateRoomCommand.class);
        final var roomCode = "ABCD";

        // When
        when(this.roomCodeGenerationPort.execute()).thenReturn(roomCode);
        final var roomJoinResult = this.createRoomUseCaseImpl.execute(createRoomCommand);

        // Then
        verify(this.roomCodeGenerationPort, times(1)).execute();
        final var room = this.inMemoryRoomStore.findByCode(roomCode).orElseThrow();
        assertEquals(roomCode, roomJoinResult.roomCode());
        assertNotNull(roomJoinResult.playerId());
        assertEquals(room, roomJoinResult.room());
    }
}
