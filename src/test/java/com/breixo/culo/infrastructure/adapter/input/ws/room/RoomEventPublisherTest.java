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

/** Tests for {@link RoomEventPublisher}. */
@ExtendWith(MockitoExtension.class)
class RoomEventPublisherTest {

  @Mock
  private SimpMessagingTemplate simpMessagingTemplate;

  @Mock
  private JoinedRoomV1ResponseMapper joinedRoomV1ResponseMapper;

  @Mock
  private RoomStateV1ResponseMapper roomStateV1ResponseMapper;

  @Mock
  private CardV1DtoMapper cardV1DtoMapper;

  @Mock
  private PlayerLookupService playerLookupService;

  @Spy
  @InjectMocks
  private RoomEventPublisher roomEventPublisher;

  /** publishJoinResult must re-send the joining player's hand (reconnect after F5). */
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
