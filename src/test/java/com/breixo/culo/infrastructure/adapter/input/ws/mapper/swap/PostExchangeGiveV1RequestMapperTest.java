package com.breixo.culo.infrastructure.adapter.input.ws.mapper.swap;

import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostExchangeGiveV1RequestDto;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** The Class Post Exchange Give V 1 Request Mapper Test. */
@ExtendWith(MockitoExtension.class)
class PostExchangeGiveV1RequestMapperTest {

    /** The post exchange give V 1 request mapper. */
    @InjectMocks
    PostExchangeGiveV1RequestMapperImpl postExchangeGiveV1RequestMapper;

    /** Test to exchange give command when request is valid then return mapped command. */
    @Test
    void testToExchangeGiveCommand_whenRequestIsValid_thenReturnMappedCommand() {
        // Given
        final var postExchangeGiveV1RequestDto = Instancio.create(PostExchangeGiveV1RequestDto.class);

        // When
        final var exchangeGiveCommand = this.postExchangeGiveV1RequestMapper
                .toExchangeGiveCommand(postExchangeGiveV1RequestDto);

        // Then
        assertEquals(postExchangeGiveV1RequestDto.getClientId(), exchangeGiveCommand.clientId());
        assertEquals(postExchangeGiveV1RequestDto.getRoomCode(), exchangeGiveCommand.roomCode());
        assertEquals(postExchangeGiveV1RequestDto.getCards().size(), exchangeGiveCommand.cards().size());
    }

    /** Test to exchange give command when request is null then return null. */
    @Test
    void testToExchangeGiveCommand_whenRequestIsNull_thenReturnNull() {
        // When / Then
        assertNull(this.postExchangeGiveV1RequestMapper.toExchangeGiveCommand(null));
    }
}
