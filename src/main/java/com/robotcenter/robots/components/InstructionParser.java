package com.robotcenter.robots.components;

import com.robotcenter.robots.domain.entities.Instruction;
import com.robotcenter.robots.domain.enums.CommandType;
import org.springframework.stereotype.Component;

/**
 * Componente encargado de transformar cadenas de texto crudas provenientes del socket en objetos de
 * dominio {@link Instruction}.
 * Realiza la validación de formato y tipos según el protocolo definido.
 */
@Component
public class InstructionParser {

    /**
     * Parsea una línea de texto siguiendo el formato "ROBOT_ID | COMANDO | PARAMS".
     * @param rawLine Cadena de texto recibida del cliente.
     * @return Una instancia de {@link Instruction} con los datos extraídos.
     * @throws IllegalArgumentException Si el formato es inválido, el ID no es numérico o el comando no existe.
     */
    public Instruction parse(String rawLine) throws IllegalArgumentException {
        if(rawLine == null || rawLine.isBlank()) {
            throw new IllegalArgumentException("Mensaje en blanco"); // Validación mínima
        }
        // Dividir por el carácter pipe
        String[] parts = rawLine.split("\\|");
        if(parts.length < 2) {
            throw new IllegalArgumentException("Formato insuficiente. Se espera ROBOT_ID | COMANDO");
        }
        try {
            int robotId = Integer.parseInt(parts[0].trim());
            CommandType command = CommandType.valueOf(parts[1].trim().toUpperCase());
            String params = (parts.length > 2) ? parts[2].trim() : "";
            return new Instruction(robotId, command, params);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El ROBOT_ID debe ser un número entero");
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Comando no reconocido");
        }
    }

}
