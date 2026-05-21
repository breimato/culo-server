package com.breixo.culo.application.usecase.game;

import com.breixo.culo.application.usecase.culoswap.CuloSwapInitiateUseCaseImpl;
import com.breixo.culo.application.usecase.culoswap.CuloSwapVoteUseCaseImpl;
import com.breixo.culo.domain.command.game.CuloSwapInitiateCommand;
import com.breixo.culo.domain.command.game.CuloSwapVoteCommand;
import com.breixo.culo.domain.model.card.Card;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.model.room.enums.PlayerRole;
import com.breixo.culo.domain.service.culoswap.CuloSwapPolicyValidationServiceImpl;
import com.breixo.culo.domain.service.culoswap.CuloSwapServiceImpl;
import com.breixo.culo.domain.service.room.PlayerLookupServiceImpl;
import com.breixo.culo.domain.service.room.RoomPhaseServiceImpl;
import com.breixo.culo.domain.service.session.GameSessionContextServiceImpl;
import com.breixo.culo.testsupport.InMemoryRoomStore;
import com.breixo.culo.testsupport.RoomTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The Class Culo Swap Vote Use Case Impl Test. */
@ExtendWith(MockitoExtension.class)
class CuloSwapVoteUseCaseImplTest {

    final InMemoryRoomStore inMemoryRoomStore = new InMemoryRoomStore();

    final PlayerLookupServiceImpl playerLookupService = new PlayerLookupServiceImpl();

    final RoomPhaseServiceImpl roomPhaseService = new RoomPhaseServiceImpl();

    CuloSwapInitiateUseCaseImpl initiateUseCase;

    CuloSwapVoteUseCaseImpl voteUseCase;

    @BeforeEach
    void setUp() {
        final var culoSwapService = new CuloSwapServiceImpl(this.playerLookupService);
        final var gameSessionContextService = new GameSessionContextServiceImpl(
                this.inMemoryRoomStore,
                this.playerLookupService,
                this.roomPhaseService);
        final var culoSwapPolicyValidationService = new CuloSwapPolicyValidationServiceImpl(this.playerLookupService);
        this.initiateUseCase = new CuloSwapInitiateUseCaseImpl(
                this.inMemoryRoomStore,
                gameSessionContextService,
                culoSwapPolicyValidationService,
                culoSwapService,
                this.roomPhaseService);
        this.voteUseCase = new CuloSwapVoteUseCaseImpl(
                this.inMemoryRoomStore,
                gameSessionContextService,
                culoSwapPolicyValidationService,
                culoSwapService,
                this.roomPhaseService);
    }

    /** Test vote when two players and target accepts then completes and approves. */
    @Test
    void testVote_whenTwoPlayersAndTargetAccepts_thenCompletesAndApproves() {
        // Given
        final var culo = RoomTestFactory.player("culo-id", "culo-client", "Culo")
                .toBuilder().role(PlayerRole.CULO).build();
        final var other = RoomTestFactory.player("other-id", "other-client", "Other")
                .toBuilder().role(PlayerRole.GANADOR).build();
        final Map<String, List<Card>> hands = new HashMap<>();
        hands.put("culo-id", new ArrayList<>());
        hands.put("other-id", new ArrayList<>());
        var room = RoomTestFactory.roomWithPlayers("ABCD", "culo-id", List.of(culo, other));
        room = RoomTestFactory.withPhase(room, GamePhase.DEALING);
        room = RoomTestFactory.withHands(room, hands);
        room = room.toBuilder()
                .gameSession(room.gameSession().toBuilder().lastCuloId("culo-id").build())
                .build();
        this.inMemoryRoomStore.seed(room);

        final var culoSwapInitiateCommand = CuloSwapInitiateCommand.builder()
                .clientId("culo-client")
                .roomCode("ABCD")
                .targetPlayerId("other-id")
                .build();
        final var culoSwapVoteCommand = CuloSwapVoteCommand.builder()
                .clientId("other-client")
                .roomCode("ABCD")
                .accept(true)
                .build();

        // When
        this.initiateUseCase.execute(culoSwapInitiateCommand);
        final var culoSwapVoteResult = this.voteUseCase.execute(culoSwapVoteCommand);

        // Then
        assertTrue(culoSwapVoteResult.completed());
        assertTrue(culoSwapVoteResult.accepted());
        assertEquals(GamePhase.DEALING, culoSwapVoteResult.room().roomLobby().phase());
        assertEquals("other-id", culoSwapVoteResult.room().gameSession().lastCuloId());
    }
}
