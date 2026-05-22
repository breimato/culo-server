package com.breixo.culo.infrastructure.adapter.input.ws.mapper.room;

import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostRoomJoinV1RequestDto;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** The Class Post Room Join V 1 Request Mapper Test. */
@ExtendWith(MockitoExtension.class)
class PostRoomJoinV1RequestMapperTest {

    /** The post room join V 1 request mapper. */
    @InjectMocks
    PostRoomJoinV1RequestMapperImpl postRoomJoinV1RequestMapper;

    /** Test to join room command when request is valid then return mapped command. */
    @Test
    void testToJoinRoomCommand_whenRequestIsValid_thenReturnMappedCommand() {
        // Given
        final var postRoomJoinV1RequestDto = Instancio.create(PostRoomJoinV1RequestDto.class);

        // When
        final var joinRoomCommand = this.postRoomJoinV1RequestMapper.toJoinRoomCommand(postRoomJoinV1RequestDto);

        // Then
        assertEquals(postRoomJoinV1RequestDto.getClientId(), joinRoomCommand.clientId());
        assertEquals(postRoomJoinV1RequestDto.getRoomCode(), joinRoomCommand.roomCode());
        assertEquals(postRoomJoinV1RequestDto.getNick(), joinRoomCommand.nick());
    }

    /** Test to join room command when request is null then return null. */
    @Test
    void testToJoinRoomCommand_whenRequestIsNull_thenReturnNull() {
        // When / Then
        assertNull(this.postRoomJoinV1RequestMapper.toJoinRoomCommand(null));
    }
}
