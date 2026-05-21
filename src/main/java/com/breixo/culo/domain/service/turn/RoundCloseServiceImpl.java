package com.breixo.culo.domain.service.turn;

import com.breixo.culo.domain.model.outcome.RoundCloseResult;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.play.PlayRuleService;
import com.breixo.culo.domain.port.input.session.GameSessionContextService;
import com.breixo.culo.domain.port.input.turn.RoundCloseService;
import com.breixo.culo.domain.port.input.turn.TurnManagementService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

/** The Class RoundCloseServiceImpl. */
@Service
@RequiredArgsConstructor
public class RoundCloseServiceImpl implements RoundCloseService {

    /** The play rule service. */
    private final PlayRuleService playRuleService;

    /** The turn management service. */
    private final TurnManagementService turnManagementService;

    /** The game session context service. */
    private final GameSessionContextService gameSessionContextService;

    /** {@inheritDoc} */
    @Override
    public RoundCloseResult closeRoundIfOthersAllPassed(final Room room) {

        final var activePlayerIds = this.gameSessionContextService.activePlayerIds(room);

        if (BooleanUtils.isFalse(this.playRuleService.isRoundOver(room.gameSession().currentRound(), activePlayerIds))) {
            return RoundCloseResult.builder()
                    .room(room)
                    .roundEnded(false)
                    .build();
        }

        final var roomAfterClose = this.turnManagementService.finishRoundAndSetOpener(room);

        return RoundCloseResult.builder()
                .room(roomAfterClose)
                .roundEnded(true)
                .build();
    }
}
