package com.robotcenter.robots.services;

import com.robotcenter.robots.core.InstructionBox;
import com.robotcenter.robots.robots.Robot;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona el ciclo de vida de los hilos de los robots.
 * Se encarga de su inicialización al arranque y su detención ordenada al cierre.
 */
@Service
public class RobotManager {

    @Value("${robots.count}")
    private int robotCount;
    private final InstructionBox instructionBox;
    private final List<Thread> robotThreads = new ArrayList<>();

    public RobotManager(InstructionBox instructionBox) {
        this.instructionBox = instructionBox;
    }

    /**
     * Inicializa y arranca los hilos de los robots configurados.
     */
    @PostConstruct
    public void startRobot() {
        System.out.println("Iniciando fábrica con " + robotCount + " robots...");
        for (int i = 1; i <= robotCount; i++) {
            // Cada robot es un hilo independiente.
            Robot robot = new Robot(i, instructionBox);
            Thread thread = new Thread(robot, "RobotThread-" + i);
            robotThreads.add(thread);
            thread.start();
        }
    }

    /**
     * Metodo invocado por Spring antes de destruir el Bean.
     * Asegura que todos los hilos finalicen antes de cerrar el proceso.
     */
    public void stopAll() {
        System.out.println("Notificando parada a los robots...");
        instructionBox.shutdown(); // Despierta a los hilos en wait()
        for (Thread t : robotThreads) {
            try {
                t.join(1000); // Espera un segundo a que cada hilo muera limpiamente
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Todos los robots se han detenido limpiamente.");
    }

}
