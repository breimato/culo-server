package com.breixo.culo.infrastructure.adapter.input.ws.mapper.game;

import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostGamePlayV1RequestDto;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** The Class Post Game Play V 1 Request Mapper Test. */
@ExtendWith(MockitoExtension.class)
class PostGamePlayV1RequestMapperTest {

    /** The post game play V 1 request mapper. */
    @InjectMocks
    PostGamePlayV1RequestMapperImpl postGamePlayV1RequestMapper;

    /** Test to play cards command when request is valid then return mapped command. */
    @Test
    void testToPlayCardsCommand_whenRequestIsValid_thenReturnMappedCommand() {
        // Given
        final var postGamePlayV1RequestDto = Instancio.create(PostGamePlayV1RequestDto.class);

        // When
        final var playCardsCommand = this.postGamePlayV1RequestMapper.toPlayCardsCommand(postGamePlayV1RequestDto);

        // Then
        assertEquals(postGamePlayV1RequestDto.getClientId(), playCardsCommand.clientId());
        assertEquals(postGamePlayV1RequestDto.getRoomCode(), playCardsCommand.roomCode());
        assertEquals(postGamePlayV1RequestDto.getCards().size(), playCardsCommand.cards().size());
    }

    /** Test to play cards command when request is null then return null. */
    @Test
    void testToPlayCardsCommand_whenRequestIsNull_thenReturnNull() {
        // When / Then
        assertNull(this.postGamePlayV1RequestMapper.toPlayCardsCommand(null));
    }
}
