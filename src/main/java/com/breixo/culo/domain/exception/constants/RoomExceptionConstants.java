package com.breixo.culo.domain.exception.constants;

public final class RoomExceptionConstants {

    public static final String ROOM_NOT_FOUND      = "CULO-ROOM-001 | Sala no encontrada";
    public static final String ROOM_FULL           = "CULO-ROOM-002 | La sala está llena";
    public static final String GAME_ALREADY_STARTED = "CULO-ROOM-003 | La partida ya ha empezado";
    public static final String NOT_HOST            = "CULO-ROOM-004 | Solo el host puede realizar esta acción";
    public static final String NOT_ENOUGH_PLAYERS  = "CULO-ROOM-005 | Se necesitan al menos 2 jugadores para empezar";
    public static final String PLAYER_NOT_IN_ROOM  = "CULO-ROOM-006 | No estás en esta sala";
    public static final String UNIQUE_CODE_GENERATION_FAILED = "CULO-ROOM-007 | No se pudo generar un código de sala único";
    public static final String ROOM_CLOSED            = "CULO-ROOM-008 | La sala ha sido cerrada";

    private RoomExceptionConstants() {}
}
