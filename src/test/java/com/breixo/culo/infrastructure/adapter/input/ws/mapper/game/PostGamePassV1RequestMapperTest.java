package com.breixo.culo.infrastructure.adapter.input.ws.mapper.game;

import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostGamePassV1RequestDto;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * The Class PostGamePassV1RequestMapperTest.
 */
@ExtendWith(MockitoExtension.class)
class PostGamePassV1RequestMapperTest {

    /** The post game pass V 1 request mapper. */
    @InjectMocks
    PostGamePassV1RequestMapperImpl postGamePassV1RequestMapper;

    /**
	 * Test to pass command when request is valid then return mapped command.
	 */
    @Test
    void testToPassCommand_whenRequestIsValid_thenReturnMappedCommand() {
        
        // Given
        final var postGamePassV1RequestDto = Instancio.create(PostGamePassV1RequestDto.class);

        // When
        final var passCommand = this.postGamePassV1RequestMapper.toPassCommand(postGamePassV1RequestDto);

        // Then
        assertEquals(postGamePassV1RequestDto.getClientId(), passCommand.clientId());
        assertEquals(postGamePassV1RequestDto.getRoomCode(), passCommand.roomCode());
    }

    /**
	 * Test to pass command when request is null then return null.
	 */
    @Test
    void testToPassCommand_whenRequestIsNull_thenReturnNull() {
        // When / Then
        assertNull(this.postGamePassV1RequestMapper.toPassCommand(null));
    }
}
