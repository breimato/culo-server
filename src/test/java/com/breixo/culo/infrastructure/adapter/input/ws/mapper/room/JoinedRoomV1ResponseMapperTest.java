package com.breixo.culo.infrastructure.adapter.input.ws.mapper.room;

import com.breixo.culo.domain.model.room.RoomJoinResult;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** The Class Joined Room V 1 Response Mapper Test. */
@ExtendWith(MockitoExtension.class)
class JoinedRoomV1ResponseMapperTest {

    /** The joined room V 1 response mapper. */
    @InjectMocks
    JoinedRoomV1ResponseMapperImpl joinedRoomV1ResponseMapper;

    /** Test to joined room V 1 response dto when result is valid then return mapped dto. */
    @Test
    void testToJoinedRoomV1ResponseDto_whenResultIsValid_thenReturnMappedDto() {
        // Given
        final var roomJoinResult = Instancio.create(RoomJoinResult.class);

        // When
        final var joinedRoomV1ResponseDto = this.joinedRoomV1ResponseMapper.toJoinedRoomV1ResponseDto(roomJoinResult);

        // Then
        assertEquals(roomJoinResult.roomCode(), joinedRoomV1ResponseDto.getRoomCode());
    }

    /** Test to joined room V 1 response dto when result is null then return null. */
    @Test
    void testToJoinedRoomV1ResponseDto_whenResultIsNull_thenReturnNull() {
        // When / Then
        assertNull(this.joinedRoomV1ResponseMapper.toJoinedRoomV1ResponseDto(null));
    }
}
