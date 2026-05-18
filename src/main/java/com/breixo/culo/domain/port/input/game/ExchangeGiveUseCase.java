package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.command.game.ExchangeGiveCommand;
import com.breixo.culo.domain.model.Room;

public interface ExchangeGiveUseCase {

    Room execute(ExchangeGiveCommand exchangeGiveCommand);
}
