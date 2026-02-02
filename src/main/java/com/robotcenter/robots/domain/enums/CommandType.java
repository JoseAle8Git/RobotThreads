package com.robotcenter.robots.domain.enums;

/**
 * Define el conjunto de comandos válidos que el sistema puede procesar.
 * Es la base del protocolo de comunicación entre el cliente y los robots.
 */
public enum CommandType {

    /** Avanzar unidades */
    MOVE,
    /** Girar a la izquierda */
    TURN_LEFT,
    /** Recoger un objeto */
    PICK,
    /** Consultar estado del robot */
    STATUS,
    /** Orden de apagado total del sistema */
    SHUTDOWN

}
