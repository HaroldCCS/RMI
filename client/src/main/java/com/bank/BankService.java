package com.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Copia de la interfaz remota para el cliente.
 * Debe ser idéntica a la del servidor.
 */
public interface BankService extends Remote {
    boolean login(String username, String password) throws RemoteException;
    double  getBalance(String username)              throws RemoteException;
    boolean withdraw(String username, double amount) throws RemoteException;
}
