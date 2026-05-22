package com.breixo.culo.infrastructure.adapter.input.ws.mapper.swap;

import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostCuloSwapInitiateV1RequestDto;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** The Class Post Culo Swap Initiate V 1 Request Mapper Test. */
@ExtendWith(MockitoExtension.class)
class PostCuloSwapInitiateV1RequestMapperTest {

    /** The post culo swap initiate V 1 request mapper. */
    @InjectMocks
    PostCuloSwapInitiateV1RequestMapperImpl postCuloSwapInitiateV1RequestMapper;

    /** Test to culo swap initiate command when request is valid then return mapped command. */
    @Test
    void testToCuloSwapInitiateCommand_whenRequestIsValid_thenReturnMappedCommand() {
        // Given
        final var postCuloSwapInitiateV1RequestDto = Instancio.create(PostCuloSwapInitiateV1RequestDto.class);

        // When
        final var culoSwapInitiateCommand = this.postCuloSwapInitiateV1RequestMapper
                .toCuloSwapInitiateCommand(postCuloSwapInitiateV1RequestDto);

        // Then
        assertEquals(postCuloSwapInitiateV1RequestDto.getClientId(), culoSwapInitiateCommand.clientId());
        assertEquals(postCuloSwapInitiateV1RequestDto.getRoomCode(), culoSwapInitiateCommand.roomCode());
        assertEquals(postCuloSwapInitiateV1RequestDto.getTargetPlayerId(), culoSwapInitiateCommand.targetPlayerId());
    }

    /** Test to culo swap initiate command when request is null then return null. */
    @Test
    void testToCuloSwapInitiateCommand_whenRequestIsNull_thenReturnNull() {
        // When / Then
        assertNull(this.postCuloSwapInitiateV1RequestMapper.toCuloSwapInitiateCommand(null));
    }
}
