package com.bank.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.bank.BankService;

/**
 * Punto de entrada del cliente RMI.
 * Conecta al registry RMI y lanza la pantalla de Login.
 */
public class BankClient {

    public static void main(String[] args) {
        String serverHost = System.getProperty("RMI_HOST",
                               System.getenv().getOrDefault("RMI_HOST", "localhost"));
        int    serverPort = Integer.parseInt(System.getProperty("RMI_PORT",
                               System.getenv().getOrDefault("RMI_PORT", "1099")));

        System.out.println("[BankClient] Conectando a RMI en " + serverHost + ":" + serverPort);

        BankService service = null;

        // Reintentar la conexión hasta 20 veces (el server puede tardar en levantar)
        for (int i = 0; i < 20; i++) {
            try {
                Registry registry = LocateRegistry.getRegistry(serverHost, serverPort);
                service = (BankService) registry.lookup("BankService");
                System.out.println("[BankClient] Conectado al servidor RMI ✓");
                service.getBalance("alice");
                break;
            } catch (Exception e) {
                System.out.printf("[BankClient] Intento %d/20 – servidor no disponible (%s)%n", i + 1, e.getMessage());
                try { Thread.sleep(3000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }
        }

        if (service == null) {
            System.err.println("[BankClient] No se pudo conectar al servidor RMI. Saliendo.");
            System.exit(1);
        }

        final BankService finalService = service;
        // Lanzar la GUI en el Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> new LoginScreen(finalService).setVisible(true));
    }
}
