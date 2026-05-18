package com.breixo.culo.domain.port.input.room;

import com.breixo.culo.domain.command.room.StartGameCommand;
import com.breixo.culo.domain.model.Room;

public interface StartGameUseCase {

  Room execute(StartGameCommand startGameCommand);
}
