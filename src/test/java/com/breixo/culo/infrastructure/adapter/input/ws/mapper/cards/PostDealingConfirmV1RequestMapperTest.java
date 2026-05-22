package com.breixo.culo.infrastructure.adapter.input.ws.mapper.cards;

import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostDealingConfirmV1RequestDto;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * The Class PostDealingConfirmV1RequestMapperTest.
 */
@ExtendWith(MockitoExtension.class)
class PostDealingConfirmV1RequestMapperTest {

    /** The post dealing confirm V 1 request mapper. */
    @InjectMocks
    PostDealingConfirmV1RequestMapperImpl postDealingConfirmV1RequestMapper;

    /**
	 * Test to deal cards command when request is valid then return mapped command.
	 */
    @Test
    void testToDealCardsCommand_whenRequestIsValid_thenReturnMappedCommand() {
        
        // Given
        final var postDealingConfirmV1RequestDto = Instancio.create(PostDealingConfirmV1RequestDto.class);

        // When
        final var dealCardsCommand = this.postDealingConfirmV1RequestMapper
                .toDealCardsCommand(postDealingConfirmV1RequestDto);

        // Then
        assertEquals(postDealingConfirmV1RequestDto.getClientId(), dealCardsCommand.clientId());
        assertEquals(postDealingConfirmV1RequestDto.getRoomCode(), dealCardsCommand.roomCode());
    }

    /**
	 * Test to deal cards command when request is null then return null.
	 */
    @Test
    void testToDealCardsCommand_whenRequestIsNull_thenReturnNull() {
        // When / Then
        assertNull(this.postDealingConfirmV1RequestMapper.toDealCardsCommand(null));
    }
}
