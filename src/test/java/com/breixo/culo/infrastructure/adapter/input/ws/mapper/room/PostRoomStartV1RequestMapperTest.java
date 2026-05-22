package com.breixo.culo.infrastructure.adapter.input.ws.mapper.room;

import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostRoomStartV1RequestDto;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** The Class Post Room Start V 1 Request Mapper Test. */
@ExtendWith(MockitoExtension.class)
class PostRoomStartV1RequestMapperTest {

    /** The post room start V 1 request mapper. */
    @InjectMocks
    PostRoomStartV1RequestMapperImpl postRoomStartV1RequestMapper;

    /** Test to start game command when request is valid then return mapped command. */
    @Test
    void testToStartGameCommand_whenRequestIsValid_thenReturnMappedCommand() {
        // Given
        final var postRoomStartV1RequestDto = Instancio.create(PostRoomStartV1RequestDto.class);

        // When
        final var startGameCommand = this.postRoomStartV1RequestMapper.toStartGameCommand(postRoomStartV1RequestDto);

        // Then
        assertEquals(postRoomStartV1RequestDto.getClientId(), startGameCommand.clientId());
        assertEquals(postRoomStartV1RequestDto.getRoomCode(), startGameCommand.roomCode());
    }

    /** Test to start game command when request is null then return null. */
    @Test
    void testToStartGameCommand_whenRequestIsNull_thenReturnNull() {
        // When / Then
        assertNull(this.postRoomStartV1RequestMapper.toStartGameCommand(null));
    }
}
