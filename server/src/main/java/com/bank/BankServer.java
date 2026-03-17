package com.bank;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Punto de entrada del servidor RMI.
 * Registra BankServiceImpl en el RMI Registry en el puerto 1099.
 */
public class BankServer {

    public static void main(String[] args) {
        // Variables de entorno (inyectadas por Docker Compose)
        String dbHost = System.getenv().getOrDefault("DB_HOST", "localhost");
        String dbPort = System.getenv().getOrDefault("DB_PORT", "3306");
        String dbName = System.getenv().getOrDefault("DB_NAME", "bank");
        String dbUser = System.getenv().getOrDefault("DB_USER", "bankuser");
        String dbPass = System.getenv().getOrDefault("DB_PASS", "bankpass");

        String jdbcUrl = String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                dbHost, dbPort, dbName
        );

        // Esperar a que MySQL esté listo (máx. 30 intentos × 2s = 60s)
        System.out.println("[BankServer] Esperando conexión a la base de datos...");
        boolean connected = false;
        for (int i = 0; i < 30; i++) {
            try {
                java.sql.DriverManager.getConnection(jdbcUrl, dbUser, dbPass).close();
                connected = true;
                System.out.println("[BankServer] Base de datos disponible.");
                break;
            } catch (Exception e) {
                System.out.printf("[BankServer] Intento %d/30 – DB no disponible, esperando 2s...%n", i + 1);
                try { Thread.sleep(2000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }
        }

        if (!connected) {
            System.err.println("[BankServer] No se pudo conectar a la base de datos. Saliendo.");
            System.exit(1);
        }

        try {
            // Exponer la IP del servidor para clientes remotos
            String serverHost = System.getenv().getOrDefault("SERVER_HOST", "server");
            System.setProperty("java.rmi.server.hostname", serverHost);

            BankServiceImpl service = new BankServiceImpl(jdbcUrl, dbUser, dbPass);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("BankService", service);
            System.out.println("[BankServer] BankServer ready en puerto 1099 ✓");
        } catch (Exception e) {
            System.err.println("[BankServer] Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
