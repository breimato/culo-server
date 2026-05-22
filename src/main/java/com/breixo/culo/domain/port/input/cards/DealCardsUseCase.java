package com.breixo.culo.domain.port.input.cards;

import com.breixo.culo.domain.command.cards.DealCardsCommand;
import com.breixo.culo.domain.model.room.Room;

/** The Interface DealCardsUseCase. */
public interface DealCardsUseCase {

    /**
     * Execute.
     *
     * @param dealCardsCommand the deal cards command
     * @return the room
     */
    Room execute(DealCardsCommand dealCardsCommand);
}
