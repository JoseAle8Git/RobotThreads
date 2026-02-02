package com.robotcenter.robots.components;

import com.robotcenter.robots.domain.entities.Instruction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Monitor que actúa como búfer intermedio entre el servidor de red y los robots.
 * Implementa el patrón Productor-Consumidor garantizando que el acceso a la cola de instrucciones sea thread-safe.
 */
@Component
public class InstructionBox {

    /** Cola interna de instrucciones. Se usa ArrayList por simplicidad al buscar por ID. */
    private final List<Instruction> queue = new ArrayList<>();
    /** Flag que indica si el sistema está en proceso de apagado. */
    private boolean isShutdown = false;

    /**
     * Inserta una instrucción en la cola.
     * Utiliza {@code notifyAll()} para despertar a cualquier robot bloqueado en {@code wait()}.
     * @param instruction La instrucción a encolar.
     */
    public synchronized void put(Instruction instruction) {
        queue.add(instruction);
        // Despierta a los robots que están esperando instrucciones.
        notifyAll();
    }

    /**
     * Extrae de forma bloqueante la primera instrucción que coincida con el robotId.
     * Diseño: Se utiliza un bucle {@code while} con {@code wait()} para evitar la espera activa (busy wait),
     * liberando el monitor mientras no haya datos relevantes.
     * @param robotId ID del robot que solicita la instrucción.
     * @return La instrucción encontrada o {@code null} si el sistema se apaga.
     * @throws InterruptedException Si el hilo es interrumpido mientras espera.
     */
    public synchronized Instruction takeFor(Integer robotId) throws InterruptedException {
        while (!isShutdown) {
            for (int i = 0; i < queue.size(); i++) {
                if (queue.get(i).robotId() == robotId) {
                    return queue.remove(i);
                }
            }
            wait(); // Espera masiva hasta que se inserte algo nuevo o se apague el sistema
        }
        return null;
    }

    /**
     * Activa el estado de apagado y notifica a todos los hilos en espera para que finalicen su ejecución de forma limpia.
     */
    public synchronized void shutdown() {
        this.isShutdown = true;
        notifyAll();
    }

}
