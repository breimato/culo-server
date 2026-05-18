package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.command.game.PlayCardsCommand;
import com.breixo.culo.domain.model.game.PlayResult;

public interface PlayCardsUseCase {

    PlayResult execute(PlayCardsCommand playCardsCommand);
}
