package com.robotcenter.robots.services;

import com.robotcenter.robots.components.InstructionBox;
import com.robotcenter.robots.components.InstructionParser;
import com.robotcenter.robots.handlers.ClientHandler;
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
        // Hilo principal del servidor de sockets.
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor iniciado en puerto " + port);
            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = serverSocket.accept();
                // Cada cliente es gestionado por un hilo.
                new Thread(new ClientHandler(clientSocket, instructionBox, parser)).start();
            }
        } catch (IOException ex) {
            System.err.println("Error en el servidor: " + ex.getMessage());
        }
    }
}
