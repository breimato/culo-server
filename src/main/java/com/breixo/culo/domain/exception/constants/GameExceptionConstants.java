package com.breixo.culo.domain.exception.constants;

public final class GameExceptionConstants {

    public static final String NOT_YOUR_TURN       = "CULO-GAME-001 | No es tu turno";
    public static final String ILLEGAL_PLAY        = "CULO-GAME-002 | Jugada ilegal";
    public static final String CARDS_NOT_IN_HAND   = "CULO-GAME-003 | No tienes esas cartas en mano";
    public static final String WRONG_PHASE         = "CULO-GAME-004 | Acción no permitida en esta fase";
    public static final String NOT_CULO            = "CULO-GAME-005 | Solo el culo puede realizar esta acción";
    public static final String SWAP_ALREADY_ACTIVE = "CULO-GAME-006 | Ya hay una votación de transferencia activa";
    public static final String SWAP_ALREADY_VOTED  = "CULO-GAME-009 | Ya has votado en esta transferencia";
    public static final String NOT_IN_EXCHANGE     = "CULO-GAME-007 | No eres participante de este intercambio";
    public static final String INVALID_EXCHANGE    = "CULO-GAME-008 | Selección de cartas inválida para el intercambio";

    private GameExceptionConstants() {
    }
}
