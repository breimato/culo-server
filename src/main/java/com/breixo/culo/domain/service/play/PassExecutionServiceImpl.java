package com.breixo.culo.domain.service.play;

import com.breixo.culo.domain.model.play.PassResult;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.play.PassExecutionService;
import com.breixo.culo.domain.port.input.quad.QuadDiscardService;
import com.breixo.culo.domain.port.input.round.RoundService;
import com.breixo.culo.domain.port.input.turn.RoundCloseService;
import com.breixo.culo.domain.port.input.turn.TurnManagementService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

/** The Class PassExecutionServiceImpl. */
@Service
@RequiredArgsConstructor
public class PassExecutionServiceImpl implements PassExecutionService {

    /** The quad discard service. */
    private final QuadDiscardService quadDiscardService;

    /** The round service. */
    private final RoundService roundService;

    /** The turn management service. */
    private final TurnManagementService turnManagementService;

    /** The round close service. */
    private final RoundCloseService roundCloseService;

    /** {@inheritDoc} */
    @Override
    public PassResult execute(final Room room, final Player player) {

        final var discardQuadsResult = this.quadDiscardService.discardQuads(room, player.id());
        var roomAfterPass = discardQuadsResult.room();

        final var updatedRound = this.roundService.registerPass(
                roomAfterPass.gameSession().currentRound(),
                player.id());
        roomAfterPass = roomAfterPass.toBuilder()
                .gameSession(roomAfterPass.gameSession().toBuilder()
                        .currentRound(updatedRound)
                        .build())
                .build();

        var roundCloseResult = this.roundCloseService.closeRoundIfOthersAllPassed(roomAfterPass);
        roomAfterPass = roundCloseResult.room();
        var roundEnded = roundCloseResult.roundEnded();

        if (BooleanUtils.isFalse(roundEnded)) {
            roomAfterPass = this.turnManagementService.advanceTurn(roomAfterPass, false);
            roundCloseResult = this.roundCloseService.closeRoundIfOthersAllPassed(roomAfterPass);
            roomAfterPass = roundCloseResult.room();
            roundEnded = roundCloseResult.roundEnded();
        }

        return PassResult.builder()
                .room(roomAfterPass)
                .playerId(player.id())
                .roundEnded(roundEnded)
                .build();
    }
}
