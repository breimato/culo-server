package com.breixo.culo.infrastructure.adapter.input.ws.room;

import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.RoomJoinResult;
import com.breixo.culo.domain.port.input.room.PlayerLookupService;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.JoinedRoomV1ResponseDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.cards.CardV1DtoMapper;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.room.JoinedRoomV1ResponseMapper;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.room.RoomStateV1ResponseMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The Class RoomEventPublisherTest.
 */
@ExtendWith(MockitoExtension.class)
class RoomEventPublisherTest {

  /** The simp messaging template. */
  @Mock
  private SimpMessagingTemplate simpMessagingTemplate;

  /** The joined room V 1 response mapper. */
  @Mock
  private JoinedRoomV1ResponseMapper joinedRoomV1ResponseMapper;

  /** The room state V 1 response mapper. */
  @Mock
  private RoomStateV1ResponseMapper roomStateV1ResponseMapper;

  /** The card V 1 dto mapper. */
  @Mock
  private CardV1DtoMapper cardV1DtoMapper;

  /** The player lookup service. */
  @Mock
  private PlayerLookupService playerLookupService;

  /** The room event publisher. */
  @Spy
  @InjectMocks
  private RoomEventPublisher roomEventPublisher;

  /**
	 * Publish join result publishes hand update for joining player.
	 */
  @Test
  void publishJoinResult_publishesHandUpdateForJoiningPlayer() {
    final var roomJoinResult = Instancio.create(RoomJoinResult.class);
    final var joinedRoomV1ResponseDto = Instancio.create(JoinedRoomV1ResponseDto.class);
    final var player = Instancio.create(Player.class);

    when(this.joinedRoomV1ResponseMapper.toJoinedRoomV1ResponseDto(roomJoinResult))
        .thenReturn(joinedRoomV1ResponseDto);
    when(this.playerLookupService.findPlayerById(roomJoinResult.room(), roomJoinResult.playerId()))
        .thenReturn(Optional.of(player));

    doNothing().when(this.roomEventPublisher).publishJoinedRoom(any(), any());
    doNothing().when(this.roomEventPublisher).publishJoinedRoomToClient(any(), any());
    doNothing().when(this.roomEventPublisher).publishRoomState(any());
    doNothing().when(this.roomEventPublisher).publishHandUpdate(any(), any());

    this.roomEventPublisher.publishJoinResult(roomJoinResult);

    verify(this.roomEventPublisher, times(1))
        .publishHandUpdate(roomJoinResult.room(), roomJoinResult.playerId());
  }
}
