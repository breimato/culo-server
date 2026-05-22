package com.breixo.culo.infrastructure.adapter.input.ws.mapper.swap;

import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostCuloSwapVoteV1RequestDto;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** The Class Post Culo Swap Vote V 1 Request Mapper Test. */
@ExtendWith(MockitoExtension.class)
class PostCuloSwapVoteV1RequestMapperTest {

    /** The post culo swap vote V 1 request mapper. */
    @InjectMocks
    PostCuloSwapVoteV1RequestMapperImpl postCuloSwapVoteV1RequestMapper;

    /** Test to culo swap vote command when request is valid then return mapped command. */
    @Test
    void testToCuloSwapVoteCommand_whenRequestIsValid_thenReturnMappedCommand() {
        // Given
        final var postCuloSwapVoteV1RequestDto = Instancio.create(PostCuloSwapVoteV1RequestDto.class);

        // When
        final var culoSwapVoteCommand = this.postCuloSwapVoteV1RequestMapper
                .toCuloSwapVoteCommand(postCuloSwapVoteV1RequestDto);

        // Then
        assertEquals(postCuloSwapVoteV1RequestDto.getClientId(), culoSwapVoteCommand.clientId());
        assertEquals(postCuloSwapVoteV1RequestDto.getRoomCode(), culoSwapVoteCommand.roomCode());
        assertEquals(postCuloSwapVoteV1RequestDto.getAccept(), culoSwapVoteCommand.accept());
    }

    /** Test to culo swap vote command when request is null then return null. */
    @Test
    void testToCuloSwapVoteCommand_whenRequestIsNull_thenReturnNull() {
        // When / Then
        assertNull(this.postCuloSwapVoteV1RequestMapper.toCuloSwapVoteCommand(null));
    }
}
