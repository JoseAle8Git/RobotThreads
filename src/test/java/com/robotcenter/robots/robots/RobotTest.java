package com.robotcenter.robots.robots;

import com.robotcenter.robots.core.InstructionBox;
import com.robotcenter.robots.domain.CommandType;
import com.robotcenter.robots.domain.Instruction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * Test unitario para la lógica del Robot.
 * Verifica que el robot solicite instrucciones para su ID y finalice correctamente.
 */
public class RobotTest {

    @Test
    @DisplayName("El robot debe solicitar instrucciones específicas para su ID al buzón")
    void testRobotAsksForCorrectId() throws InterruptedException {
        // 1. Setup: Mock del buzón e instancia del robot 7
        InstructionBox mockBox = mock(InstructionBox.class);
        int robotId = 7;
        Robot robot = new Robot(robotId, mockBox);

        // 2. Se define que el buzón devuelva una instrucción y luego null (para parar el bucle)
        Instruction instr = new Instruction(robotId, CommandType.MOVE, "10");
        when(mockBox.takeFor(robotId)).thenReturn(instr).thenReturn(null);

        // 3. Ejecución: Se corre el robot en el hilo actual
        robot.run();

        // 4. Verificación: ¿El robot llamó a takeFor con su ID exacto?
        verify(mockBox, atLeastOnce()).takeFor(robotId);
        // Verificamos que no haya preguntado por otros IDs
        verify(mockBox, never()).takeFor(argThat(id -> id != robotId));
    }

    @Test
    @DisplayName("El robot debe finalizar el hilo si el buzón devuelve null (Shutdown)")
    void testRobotStopsOnShutdown() throws InterruptedException {
        InstructionBox mockBox = mock(InstructionBox.class);
        Robot robot = new Robot(1, mockBox);

        // Simulamos que el sistema está apagandose
        when(mockBox.takeFor(anyInt())).thenReturn(null);

        // Usamos assertTimeout para asegurar que el bucle no es infinito
        assertTimeoutPreemptively(Duration.ofMillis(500), robot::run, "El hilo del robot debería haber terminado al recibir null");
    }

}
