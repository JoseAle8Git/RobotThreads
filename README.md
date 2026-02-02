# Central de Control de Robots (Socket + Hilos)

Sistema distribuido para la gestión de robots industriales mediante un protocolo de red TCP.

## Requisitos
* Java 17 o superior
* Maven 3.8+

## Cómo ejecutar el servidor
1. Compilar el proyecto: `mvn clean install`
2. Ejecutar la aplicación: `mvn spring-boot:run`
3. El servidor escuchará en el puerto `8080`.

## Cómo probar
Puedes usar **telnet** o **nc (netcat)**:
- Conexión: `telnet localhost 8080`
- Comando ejemplo: `1 | MOVE | 10`
- Comando apagado: `0 | SHUTDOWN |`

## Diseño de Sincronización
Se ha implementado un **Monitor** en la clase `InstructionBox` utilizando `wait()` y `notifyAll()` para evitar la espera activa (busy wait). Los robots permanecen bloqueados hasta que el servidor deposita una instrucción dirigida específicamente a su ID.