package com.robotcenter.robots.robots;

import com.robotcenter.robots.core.InstructionBox;
import com.robotcenter.robots.domain.Instruction;

/**
 * Representa un hilo de ejecución independiente para un robot de la fábrica.
 * Cada robot consume únicamente las instrucciones dirigidas a su robotId de forma concurrente.
 */
public class Robot implements Runnable {

    private final int robotId;
    private final InstructionBox box;

    /**
     * Constructor para la entidad Robot.
     * @param robotId Identificador numérico único asignado a este robot.
     * @param box Referencia al buzón compartido de instrucciones.
     */
    public Robot(int robotId, InstructionBox box) {
        this.robotId = robotId;
        this.box = box;
    }

    /**
     * Bucle principal de ejecución del robot.
     * Implementa una espera bloqueante en el buzón; si no hay tareas, el hilo se suspende para no consumir CPU innecesariamente
     * (evita busy wait).
     */
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Extrae y elimina la instrucción para este robotId.
                Instruction instruction = box.takeFor(robotId);

                // Si el buzón devuelve null, el sistema está en proceso de apagado.
                if (instruction == null) break; // Señal de apagado

                // Simulación de acción (100-300ms)
                long duration = (long) (Math.random() * 200 + 100);
                System.out.printf("[Robot-%d] Ejecutando: %s %s | Duración: %dms%n",
                        robotId, instruction.command(), instruction.params(), duration);

                Thread.sleep(duration);
            }
        } catch (InterruptedException e) {
            // Manejo de interrupción limpia
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("[ROBOT-" + robotId + "] Hilo finalizado.");
        }
    }
}
