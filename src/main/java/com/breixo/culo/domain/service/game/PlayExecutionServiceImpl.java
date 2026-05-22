package com.breixo.culo.domain.service.game;

import com.breixo.culo.domain.model.game.Play;
import com.breixo.culo.domain.model.game.PlayExecutionResult;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.game.PlayExecutionService;
import com.breixo.culo.domain.port.input.game.HandOnPlayService;
import com.breixo.culo.domain.port.input.game.PostPlayTurnService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

/**
 * The Class PlayExecutionServiceImpl.
 */
@Service
@RequiredArgsConstructor
public class PlayExecutionServiceImpl implements PlayExecutionService {

    /** The play hand update service. */
    private final HandOnPlayService playHandUpdateService;

    /** The play turn follow up service. */
    private final PostPlayTurnService playTurnFollowUpService;

    /** {@inheritDoc} */
    @Override
    public PlayExecutionResult execute(final Room room, final Player player, final Play play) {

        final var handUpdateOutcome = this.playHandUpdateService.apply(room, player, play);

        final var playFlags = handUpdateOutcome.playFlags();

        final var turnOutcome = this.playTurnFollowUpService.apply(
                handUpdateOutcome.room(),
                player,
                handUpdateOutcome.playFlags());

        return PlayExecutionResult.builder()
                .room(turnOutcome.room())
                .plin(playFlags.plin() && BooleanUtils.isFalse(playFlags.isAsOros()))
                .roundClosed(playFlags.isAsOros() || turnOutcome.roundClosedByPlin())
                .gameFinished(turnOutcome.gameFinished())
                .build();
    }
}
