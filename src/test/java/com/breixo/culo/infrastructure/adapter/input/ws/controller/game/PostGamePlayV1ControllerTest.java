package com.breixo.culo.infrastructure.adapter.input.ws.controller.game;

import com.breixo.culo.domain.command.game.PlayCardsCommand;
import com.breixo.culo.domain.model.game.PlayResult;
import com.breixo.culo.domain.port.input.game.PlayCardsUseCase;
import com.breixo.culo.infrastructure.adapter.input.ws.room.RoomAckCoordinator;
import com.breixo.culo.infrastructure.adapter.input.ws.room.RoomEventPublisher;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostGamePlayV1RequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.game.PostGamePlayV1RequestMapper;
import com.breixo.culo.infrastructure.adapter.input.ws.support.game.PlayFollowUpSupport;
import com.breixo.culo.infrastructure.adapter.input.ws.support.common.WsInboundControllerTestSupport;
import com.breixo.culo.infrastructure.adapter.input.ws.support.common.WsInboundExceptionSupport;
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

import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The Class PostGamePlayV1ControllerTest.
 */
@ExtendWith(MockitoExtension.class)
class PostGamePlayV1ControllerTest {

  /** The object mapper. */
  final ObjectMapper objectMapper = new ObjectMapper();

  /** The mock mvc. */
  MockMvc mockMvc;

  /** The post game play V 1 controller. */
  @InjectMocks
  PostGamePlayV1Controller postGamePlayV1Controller;

  /** The play cards use case. */
  @Mock
  PlayCardsUseCase playCardsUseCase;

  /** The post game play V 1 request mapper. */
  @Mock
  PostGamePlayV1RequestMapper postGamePlayV1RequestMapper;

  /** The room event publisher. */
  @Mock
  RoomEventPublisher roomEventPublisher;

  /** The room ack coordinator. */
  @Mock
  RoomAckCoordinator roomAckCoordinator;

  /** The play follow up support. */
  @Mock
  PlayFollowUpSupport playFollowUpSupport;

  /** The ws inbound exception support. */
  @Mock
  WsInboundExceptionSupport wsInboundExceptionSupport;

  /**
	 * Sets the up.
	 */
  @BeforeEach
  void setUp() {
    this.mockMvc = WsInboundControllerTestSupport.standaloneMockMvc(
        this.postGamePlayV1Controller, this.wsInboundExceptionSupport, this.objectMapper);
  }

  /**
	 * Test post game play V 1 when request is valid then publish play made.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testPostGamePlayV1_whenRequestIsValid_thenPublishPlayMade() throws Exception {
    // Given
    final var postGamePlayV1RequestDto = Instancio.create(PostGamePlayV1RequestDto.class);
    final var playCardsCommand = Instancio.create(PlayCardsCommand.class);
    final var playResult = Instancio.create(PlayResult.class);
    final var string = Instancio.create(String.class);
    final var room = playResult.room();
    final var roomCode = room.roomLobby().code();
    final Runnable runnable = () -> this.playFollowUpSupport.publishPlayFollowUp(roomCode, playResult);

    // When
    when(this.postGamePlayV1RequestMapper.toPlayCardsCommand(postGamePlayV1RequestDto)).thenReturn(playCardsCommand);
    when(this.playCardsUseCase.execute(playCardsCommand)).thenReturn(playResult);
    when(this.playFollowUpSupport.playFollowUpTask(roomCode, playResult)).thenReturn(runnable);
    when(this.roomAckCoordinator.awaitAllConnected(room, runnable)).thenReturn(string);
    doNothing().when(this.roomEventPublisher).publishPlayMade(room, playResult, string);

    this.mockMvc.perform(post(WsInboundDestinationConstants.POST_GAME_PLAY_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(postGamePlayV1RequestDto)))
        .andExpect(status().isNoContent());

    // Then
    verify(this.postGamePlayV1RequestMapper, times(1)).toPlayCardsCommand(postGamePlayV1RequestDto);
    verify(this.playCardsUseCase, times(1)).execute(playCardsCommand);
    verify(this.playFollowUpSupport, times(1)).playFollowUpTask(roomCode, playResult);
    verify(this.roomAckCoordinator, times(1)).awaitAllConnected(room, runnable);
    verify(this.roomEventPublisher, times(1)).publishPlayMade(room, playResult, string);
  }

  /**
	 * Test post game play V 1 when use case fails then publish error to client.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testPostGamePlayV1_whenUseCaseFails_thenPublishErrorToClient() throws Exception {
    // Given
    final var postGamePlayV1RequestDto = Instancio.create(PostGamePlayV1RequestDto.class);
    final var playCardsCommand = Instancio.create(PlayCardsCommand.class);
    final var gameException = new GameException(GameExceptionConstants.WRONG_PHASE);

    // When
    when(this.postGamePlayV1RequestMapper.toPlayCardsCommand(postGamePlayV1RequestDto)).thenReturn(playCardsCommand);
    doThrow(gameException).when(this.playCardsUseCase).execute(playCardsCommand);

    this.mockMvc.perform(post(WsInboundDestinationConstants.POST_GAME_PLAY_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(postGamePlayV1RequestDto)))
        .andExpect(status().isNoContent());

    // Then
    verify(this.postGamePlayV1RequestMapper, times(1)).toPlayCardsCommand(postGamePlayV1RequestDto);
    verify(this.playCardsUseCase, times(1)).execute(playCardsCommand);
    verify(this.wsInboundExceptionSupport, times(1))
        .publishErrorToClient(postGamePlayV1RequestDto.getClientId(), gameException);
  }
}
