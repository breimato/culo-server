package com.breixo.culo.infrastructure.adapter.input.ws.support;

import com.breixo.culo.domain.model.culoswap.CuloSwapVoteResult;
import com.breixo.culo.infrastructure.adapter.input.ws.RoomEventPublisher;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

/** The Class CuloSwapVoteFollowUpSupport. */
@Component
@RequiredArgsConstructor
public class CuloSwapVoteFollowUpSupport {

    /** The room event publisher. */
    private final RoomEventPublisher roomEventPublisher;

    /**
     * Publish vote follow up.
     *
     * @param culoSwapVoteResult the culo swap vote result
     */
    public void publishVoteFollowUp(final CuloSwapVoteResult culoSwapVoteResult) {
        this.roomEventPublisher.publishRoomState(culoSwapVoteResult.room());

        if (BooleanUtils.isFalse(culoSwapVoteResult.completed())) {
            return;
        }

        this.roomEventPublisher.publishCuloSwapResult(
                culoSwapVoteResult.room(),
                culoSwapVoteResult.accepted());

        if (BooleanUtils.isTrue(culoSwapVoteResult.accepted())) {
            this.roomEventPublisher.publishAllHands(culoSwapVoteResult.room());
        }
    }
}
