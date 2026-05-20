package com.breixo.culo.infrastructure.adapter.input.ws.controller.game;

import com.breixo.culo.domain.model.Player;
import com.breixo.culo.domain.model.Room;
import com.breixo.culo.domain.port.output.room.RoomPersistencePort;
import com.breixo.culo.infrastructure.adapter.input.ws.RoomAckCoordinator;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostGameAckV1RequestDto;
import com.breixo.culo.infrastructure.config.WsInboundDestinationConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The Class PostGameAckV1ControllerTest.
 */
@ExtendWith(MockitoExtension.class)
class PostGameAckV1ControllerTest {

  /** The object mapper. */
  final ObjectMapper objectMapper = new ObjectMapper();

  /** The mock mvc. */
  MockMvc mockMvc;

  /** The post game ack V 1 controller. */
  @InjectMocks
  PostGameAckV1Controller postGameAckV1Controller;

  /** The room persistence port. */
  @Mock
  RoomPersistencePort roomPersistencePort;

  /** The room ack coordinator. */
  @Mock
  RoomAckCoordinator roomAckCoordinator;

  /** The room. */
  @Mock
  Room room;

  /**
	 * Sets the up.
	 */
  @BeforeEach
  void setUp() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(this.postGameAckV1Controller).build();
  }

  /**
	 * Test post game ack V 1 when player exists then record ack.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testPostGameAckV1_whenPlayerExists_thenRecordAck() throws Exception {
    // Given
    final var postGameAckV1RequestDto = Instancio.create(PostGameAckV1RequestDto.class);
    final var player = Instancio.create(Player.class);
    final var roomCode = postGameAckV1RequestDto.getRoomCode();
    final var clientId = postGameAckV1RequestDto.getClientId();
    final var eventId = postGameAckV1RequestDto.getEventId();

    // When
    when(this.roomPersistencePort.findByCode(roomCode)).thenReturn(Optional.of(this.room));
    when(this.room.findPlayerByClientId(clientId)).thenReturn(Optional.of(player));

    this.mockMvc.perform(post(WsInboundDestinationConstants.POST_GAME_ACK_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(postGameAckV1RequestDto)))
        .andExpect(status().isNoContent());

    // Then
    verify(this.roomAckCoordinator, times(1)).recordAck(roomCode, eventId, player.getId());
  }

  /**
	 * Test post game ack V 1 when room missing then do not record ack.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testPostGameAckV1_whenRoomMissing_thenDoNotRecordAck() throws Exception {
    // Given
    final var postGameAckV1RequestDto = Instancio.create(PostGameAckV1RequestDto.class);
    final var roomCode = postGameAckV1RequestDto.getRoomCode();

    // When
    when(this.roomPersistencePort.findByCode(roomCode)).thenReturn(Optional.empty());

    this.mockMvc.perform(post(WsInboundDestinationConstants.POST_GAME_ACK_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(postGameAckV1RequestDto)))
        .andExpect(status().isNoContent());

    // Then
    verifyNoInteractions(this.roomAckCoordinator);
  }
}
