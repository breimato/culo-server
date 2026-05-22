package com.breixo.culo.infrastructure.adapter.input.ws.mapper.room;

import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostRoomCreateV1RequestDto;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** The Class Post Room Create V 1 Request Mapper Test. */
@ExtendWith(MockitoExtension.class)
class PostRoomCreateV1RequestMapperTest {

    /** The post room create V 1 request mapper. */
    @InjectMocks
    PostRoomCreateV1RequestMapperImpl postRoomCreateV1RequestMapper;

    /** Test to create room command when request is valid then return mapped command. */
    @Test
    void testToCreateRoomCommand_whenRequestIsValid_thenReturnMappedCommand() {
        // Given
        final var postRoomCreateV1RequestDto = Instancio.create(PostRoomCreateV1RequestDto.class);

        // When
        final var createRoomCommand = this.postRoomCreateV1RequestMapper.toCreateRoomCommand(postRoomCreateV1RequestDto);

        // Then
        assertEquals(postRoomCreateV1RequestDto.getClientId(), createRoomCommand.clientId());
        assertEquals(postRoomCreateV1RequestDto.getNick(), createRoomCommand.nick());
    }

    /** Test to create room command when request is null then return null. */
    @Test
    void testToCreateRoomCommand_whenRequestIsNull_thenReturnNull() {
        // When / Then
        assertNull(this.postRoomCreateV1RequestMapper.toCreateRoomCommand(null));
    }
}
