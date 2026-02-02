package com.robotcenter.robots.server;

import com.robotcenter.robots.core.InstructionBox;
import com.robotcenter.robots.core.InstructionParser;
import com.robotcenter.robots.domain.Instruction;
import com.robotcenter.robots.domain.CommandType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Gestor de comunicación individual para cada cliente conectado.
 * Esta clase implementa {@link Runnable} para permitir que el servidor gestione múltiples conexiones simultáneas sin bloquear
 * el puerto principal.
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final InstructionBox instructionBox;
    private final InstructionParser parser;

    /**
     * Constructor para el manejador de clientes.
     * @param socket El socket de la conexión entrante.
     * @param instructionBox El recurso compartido donde se depositan las órdenes.
     * @param parser El componente encargado de validar el protocolo de mensajes.
     */
    public ClientHandler(Socket socket, InstructionBox instructionBox, InstructionParser parser) {
        this.socket = socket;
        this.instructionBox = instructionBox;
        this.parser = parser;
    }

    /**
     * Ejecuta el ciclo de lectura de la instrucción enviada por el cliente.
     * Sigue el protocolo ROBOT_ID | COMANDO | PARAMS.
     * Responde OK si se encola correctamente o ERROR si hay fallos de validación.
     */
    @Override
    public void run() {
        // Log de conexión
        System.out.println("LOG: Cliente conectado desde " + socket.getRemoteSocketAddress());
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            String line = in.readLine();
            if(line != null) {
                try{
                    Instruction instr = parser.parse(line);
                    // Lógica especial de SHUTDOWN
                    if(instr.robotId() == 0 && instr.command() == CommandType.SHUTDOWN) {
                        out.println("OK|Sistema apagándose");
                        instructionBox.shutdown();
                    } else {
                        instructionBox.put(instr); // Encolado thread-safe
                        out.println("OK|Instrucción encolada");
                        System.out.println("Log: Instrucción recibida -> " + instr);
                    }
                } catch (IllegalArgumentException ex) {
                    out.println("ERROR|" + ex.getMessage());
                }
            }
        } catch (IOException ex) {
            System.out.println("Error de comunicación con cliente: " + ex.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {}
        }
    }

    /**
     * Asegura el cierre del socket y registra la desconexión.
     */
    private void closeSocket() {
        try {
            if(socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("LOG: Cliente desconectado.");
            }
        } catch (IOException ex) {
            System.out.println("LOG: Error al cerrar socket.");
        }
    }
}
