package com.robotcenter.robots.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Prueba de integración del sistema de control de robots.
 * Levanta el contexto de Spring Boot y realiza una comunicación real a través de sockets TCP para validar el flujo completo:
 * Cliente -> Servidor -> Parser -> Buzón.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class RobotIntegrationTest {

    @Test
    void testFullFlow() throws IOException {
        // Simulamos un cliente real enviando una instrucción al puerto 8080
        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("1 | MOVE | 50");
            String response = in.readLine();

            assertEquals("OK|Instrucción encolada", response);
        }
    }

}
