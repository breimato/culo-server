package com.breixo.culo.application.usecase.swap;

import com.breixo.culo.domain.command.swap.CuloSwapVoteCommand;
import com.breixo.culo.domain.model.swap.CuloSwapPollClosed;
import com.breixo.culo.domain.model.swap.CuloSwapVoteResponse;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.port.input.swap.CuloSwapPolicyService;
import com.breixo.culo.domain.port.input.swap.CuloService;
import com.breixo.culo.domain.port.input.swap.CuloSwapVoteUseCase;
import com.breixo.culo.domain.port.input.room.RoomPhaseService;
import com.breixo.culo.domain.port.input.room.GameContextService;
import com.breixo.culo.domain.port.output.room.RoomSavePersistencePort;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

/**
 * The Class CuloSwapVoteUseCaseImpl.
 */
@Component
@RequiredArgsConstructor
public class CuloSwapVoteUseCaseImpl implements CuloSwapVoteUseCase {

    /** The room save persistence port. */
    private final RoomSavePersistencePort roomSavePersistencePort;

    /** The game session context service. */
    private final GameContextService gameSessionContextService;

    /** The culo policy validation service. */
    private final CuloSwapPolicyService culoPolicyValidationService;

    /** The culo service. */
    private final CuloService culoService;

    /** The room phase service. */
    private final RoomPhaseService roomPhaseService;

    /** {@inheritDoc} */
    @Override
    public CuloSwapVoteResponse execute(final CuloSwapVoteCommand culoSwapVoteCommand) {

        final var gameSessionContext = this.gameSessionContextService.loadWithPhase(
                culoSwapVoteCommand.roomCode(),
                culoSwapVoteCommand.clientId(),
                GamePhase.CULO_SWAP_VOTE);

        final var room = gameSessionContext.room();
        final var player = gameSessionContext.player();
        final var playerId = player.id();

        this.culoPolicyValidationService.validateNotAlreadyVoted(room, player);

        final var voteCast = this.culoService.registerVote(
                room,
                playerId,
                culoSwapVoteCommand.accept());

        final var roomAfterVote = voteCast.room();
        final var allPlayersHaveVoted = voteCast.allPlayersHaveVoted();
        final var pollClosed = this.resolveVoteWhenAllPlayersVoted(roomAfterVote, allPlayersHaveVoted);

        final var savedRoom = this.roomSavePersistencePort.save(pollClosed.room());
        final var completed = pollClosed.votingFinished();
        final var accepted = pollClosed.swapAccepted();

        return CuloSwapVoteResponse.builder()
                .room(savedRoom)
                .votingFinished(completed)
                .swapAccepted(accepted)
                .build();
    }

    /**
	 * Resolve vote when all players voted.
	 *
	 * @param roomAfterVote       the room after vote
	 * @param allPlayersHaveVoted the all players have voted
	 * @return the culo swap poll closed
	 */
    private CuloSwapPollClosed resolveVoteWhenAllPlayersVoted(final Room roomAfterVote, final boolean allPlayersHaveVoted) {

        if (BooleanUtils.isFalse(allPlayersHaveVoted)) {
            return CuloSwapPollClosed.builder()
                    .room(roomAfterVote)
                    .votingFinished(false)
                    .swapAccepted(false)
                    .build();
        }

        final var accepted = this.culoService.isSwapApproved(roomAfterVote);
        var room = roomAfterVote;

        if (accepted) {
            room = this.culoService.applySwap(room);
        }

        room = this.culoService.clearSwap(room);
        room = this.roomPhaseService.withPhase(room, GamePhase.DEALING);

        return CuloSwapPollClosed.builder()
                .room(room)
                .votingFinished(true)
                .swapAccepted(accepted)
                .build();
    }
}
