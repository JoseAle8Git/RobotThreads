package com.robotcenter.robots.domain.entities;

import com.robotcenter.robots.domain.enums.CommandType;

/**
 * Representa una orden procesada y validad destinada a un robot específico.
 * Se utiliza como objeto inmutable (Record) para garantizar la integridad de los datos dentro del buzón compartido.
 * @param robotId Identificador único del robot destino.
 * @param command Tipo de comando a ejecutar.
 * @param params Parámetros adicionales de la instrucción.
 */
public record Instruction(

        Integer robotId,
        CommandType command,
        String params

) {
}
