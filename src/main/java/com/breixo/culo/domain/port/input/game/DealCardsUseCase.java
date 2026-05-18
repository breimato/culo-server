package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.command.game.DealCardsCommand;
import com.breixo.culo.domain.model.Room;

public interface DealCardsUseCase {

    Room execute(DealCardsCommand dealCardsCommand);
}
