package com.breixo.culo.application.usecase.culoswap;

import com.breixo.culo.domain.command.game.CuloSwapVoteCommand;
import com.breixo.culo.domain.model.culoswap.CuloSwapVoteResult;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.port.input.culoswap.CuloSwapPolicyValidationService;
import com.breixo.culo.domain.port.input.culoswap.CuloSwapService;
import com.breixo.culo.domain.port.input.game.CuloSwapVoteUseCase;
import com.breixo.culo.domain.port.input.room.RoomPhaseService;
import com.breixo.culo.domain.port.input.session.GameSessionContextService;
import com.breixo.culo.domain.port.output.room.RoomSavePersistencePort;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

/** The Class CuloSwapVoteUseCaseImpl. */
@Component
@RequiredArgsConstructor
public class CuloSwapVoteUseCaseImpl implements CuloSwapVoteUseCase {

    /** The room save persistence port. */
    private final RoomSavePersistencePort roomSavePersistencePort;

    /** The game session context service. */
    private final GameSessionContextService gameSessionContextService;

    /** The culo swap policy validation service. */
    private final CuloSwapPolicyValidationService culoSwapPolicyValidationService;

    /** The culo swap service. */
    private final CuloSwapService culoSwapService;

    /** The room phase service. */
    private final RoomPhaseService roomPhaseService;

    /** {@inheritDoc} */
    @Override
    public CuloSwapVoteResult execute(final CuloSwapVoteCommand culoSwapVoteCommand) {

        final var gameSessionContext = this.gameSessionContextService.loadWithPhase(
                culoSwapVoteCommand.roomCode(),
                culoSwapVoteCommand.clientId(),
                GamePhase.CULO_SWAP_VOTE);

        this.culoSwapPolicyValidationService.validateNotAlreadyVoted(
                gameSessionContext.room(),
                gameSessionContext.player());

        final var voteRegistrationResult = this.culoSwapService.registerCuloSwapVote(
                gameSessionContext.room(),
                gameSessionContext.player().id(),
                culoSwapVoteCommand.accept());

        var roomAfterVote = voteRegistrationResult.room();
        var completed = false;
        var accepted = false;

        if (voteRegistrationResult.allVoted()) {
            accepted = this.culoSwapService.isCuloSwapApproved(roomAfterVote);
            if (accepted) {
                roomAfterVote = this.culoSwapService.applyCuloSwap(roomAfterVote);
            }
            roomAfterVote = this.culoSwapService.clearCuloSwap(roomAfterVote);
            roomAfterVote = this.roomPhaseService.withPhase(roomAfterVote, GamePhase.DEALING);
            completed = true;
        }

        final var savedRoom = this.roomSavePersistencePort.save(roomAfterVote);

        return CuloSwapVoteResult.builder()
                .room(savedRoom)
                .completed(completed)
                .accepted(accepted)
                .build();
    }
}
