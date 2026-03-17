package com.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfaz remota RMI que define los servicios bancarios.
 */
public interface BankService extends Remote {

    /**
     * Autentica un usuario con usuario y contraseña.
     * @return true si las credenciales son válidas
     */
    boolean login(String username, String password) throws RemoteException;

    /**
     * Obtiene el saldo actual de la cuenta del usuario.
     */
    double getBalance(String username) throws RemoteException;

    /**
     * Realiza un retiro de la cuenta del usuario.
     * @return true si el retiro fue exitoso (saldo suficiente), false si no hay fondos
     */
    boolean withdraw(String username, double amount) throws RemoteException;
}
