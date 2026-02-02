package com.robotcenter.robots.server;

import com.robotcenter.robots.core.InstructionBox;
import com.robotcenter.robots.core.InstructionParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servicio principal del servidor que escucha conexiones entrantes.
 * Implementa {@link CommandLineRunner} para asegurar arrancar el bucle de aceptación de sockets al iniciar el contexto de Spring.
 */
@Service
public class RobotServer implements CommandLineRunner {

    private final InstructionBox instructionBox;
    private final InstructionParser parser;
    private final int port = 8080;

    /**
     * Constructor con inyección de dependencias.
     * @param instructionBox
     * @param parser
     */
    public RobotServer(InstructionBox instructionBox, InstructionParser parser) {
        this.instructionBox = instructionBox;
        this.parser = parser;
    }

    /**
     * Inicia el bucle de escucha del ServerSocket.
     * Cada conexión se delega a un nuevo hilo de {@link ClientHandler}.
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        // Lanzamos en un nuevo hilo para no bloquear el inicio de la aplicación
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Servidor iniciado en puerto " + port);
                while (!Thread.currentThread().isInterrupted()) {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new ClientHandler(clientSocket, instructionBox, parser)).start();
                }
            } catch (IOException ex) {
                System.err.println("Cierre del servidor: " + ex.getMessage());
            }
        }, "ServerSocket-Thread").start();
    }
}
