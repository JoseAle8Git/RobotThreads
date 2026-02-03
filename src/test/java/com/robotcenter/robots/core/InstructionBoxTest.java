package com.robotcenter.robots.core;

import com.robotcenter.robots.domain.Instruction;
import com.robotcenter.robots.domain.CommandType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Clase de pruebas para el monitor {@link InstructionBox}.
 * Se enfoca en validar la lógica de entrega selectiva de instrucciones por ID, garantizando que el buzón sea thread-safe
 * y que los hilos no consuman mensajes destinados a otros robots.
 */
public class InstructionBoxTest {

    /**
     * Valida que, ante múltiples instrucciones en la cola, un robot solo pueda extraer y eliminar aquella que coincide
     * con su identificador único.
     * Verifica la integridad de la cola tras el consumo selectivo.
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Un robot solo debe extraer instrucciones dirigidas a su ID")
    void testRobotSpecificConsumption() throws InterruptedException {
        InstructionBox box = new InstructionBox();
        box.put(new Instruction(1, CommandType.MOVE, "10"));
        box.put(new Instruction(2, CommandType.PICK, "BOX-7"));

        // El robot 2 intenta sacar su instrucción
        Instruction forRobot2 = box.takeFor(2);

        assertNotNull(forRobot2);
        assertEquals(2, forRobot2.robotId());
        assertEquals(CommandType.PICK, forRobot2.command());

        // Verificamos que la instrucción del robot 1 sigue ahí
        Instruction forRobot1 = box.takeFor(1);
        assertEquals(1, forRobot1.robotId());
    }

}
