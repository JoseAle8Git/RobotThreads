package com.robotcenter.robots.core;

import com.robotcenter.robots.domain.Instruction;
import com.robotcenter.robots.domain.CommandType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Clase de pruebas unitarias para el componente {@link InstructionParser}.
 * Valida que el protocolo de comunicación "ROBOT_ID | COMANDO | PARAMS" sea procesado correctamente y que se lancen excepciones
 * ante formatos mal formados.
 */
public class InstructionParserTest {

    private final InstructionParser parser = new InstructionParser();

    /** Verifica que la línea con formato válido sea convertida correctamente en un objeto {@link Instruction} con sus
     * campos mapeados.
     */
    @Test
    @DisplayName("Debe parsear correctamente una línea válida")
    void testParserValidLine() {
        Instruction result = parser.parse("1 | MOVE | 10");
        assertEquals(1, result.robotId());
        assertEquals(CommandType.MOVE, result.command());
        assertEquals("10", result.params());
    }

    /**
     * Prueba parametrizada que valida la robustez del parser ante diversas entradas inválidas (IDs no numéricos, comandos
     * inexistentes o formatos incompletos).
     * Asegura que el servidor pueda responder con "ERROR" ante basura.
     * @param input Cadena de texto mal formada para testear la resistencia del parser.
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "X | MOVE | 10",    // ID no numérico
            "1 | VOLAR | 10",   // Comando inexistente
            "1",                // Formato insuficiente [cite: 20, 21]
            ""                  // Mensaje vacío
    })
    @DisplayName("Debe lanzar excepción ante formatos inválidos")
    void testParseInvalidFormats(String input) {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(input));
    }

}
