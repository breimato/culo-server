package com.breixo.culo.domain.service.game;

import com.breixo.culo.domain.model.game.RoundClosure;
import com.breixo.culo.domain.model.game.PassResult;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.game.PassExecutionService;
import com.breixo.culo.domain.port.input.cards.QuadDiscardService;
import com.breixo.culo.domain.port.input.game.RoundService;
import com.breixo.culo.domain.port.input.game.RoundClosingService;
import com.breixo.culo.domain.port.input.game.TurnManagementService;
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
    private final RoundClosingService roundCloseService;

    /** {@inheritDoc} */
    @Override
    public PassResult execute(final Room room, final Player player) {

        final var playerId = player.id();
        final var discardQuadsResult = this.quadDiscardService.discardQuads(room, playerId);
        final var roomAfterQuads = discardQuadsResult.room();
        final var roomAfterPass = this.registerPassInRound(roomAfterQuads, playerId);
        final var roundClose = this.resolveRoundCloseAfterPass(roomAfterPass);

        return this.buildPassResult(roundClose.room(), playerId, roundClose.roundClosed());
    }

    private Room registerPassInRound(final Room room, final String playerId) {

        final var currentRound = room.gameSession().currentRound();
        final var updatedRound = this.roundService.registerPass(currentRound, playerId);

        return room.toBuilder()
                .gameSession(room.gameSession().toBuilder()
                        .currentRound(updatedRound)
                        .build())
                .build();
    }

    private RoundClosure resolveRoundCloseAfterPass(final Room roomAfterPass) {

        var room = roomAfterPass;
        var roundCloseResult = this.roundCloseService.closeRoundIfOthersAllPassed(room);
        var roundEnded = roundCloseResult.roundClosed();
        room = roundCloseResult.room();

        if (BooleanUtils.isFalse(roundEnded)) {
            room = this.turnManagementService.advanceTurn(room, false);
            roundCloseResult = this.roundCloseService.closeRoundIfOthersAllPassed(room);
            room = roundCloseResult.room();
            roundEnded = roundCloseResult.roundClosed();
        }

        return RoundClosure.builder()
                .room(room)
                .roundClosed(roundEnded)
                .build();
    }

    private PassResult buildPassResult(final Room room, final String playerId, final boolean roundClosed) {

        return PassResult.builder()
                .room(room)
                .playerId(playerId)
                .roundClosed(roundClosed)
                .build();
    }
}
