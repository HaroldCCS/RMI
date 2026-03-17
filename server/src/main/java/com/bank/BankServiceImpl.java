package com.bank;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementación de BankService que persiste datos en MySQL via JDBC.
 */
public class BankServiceImpl extends UnicastRemoteObject implements BankService {

    private final String jdbcUrl;
    private final String dbUser;
    private final String dbPass;

    public BankServiceImpl(String jdbcUrl, String dbUser, String dbPass) throws RemoteException {
        super(1098);   // puerto fijo: evita puerto dinámico que Docker no expone
        this.jdbcUrl = jdbcUrl;
        this.dbUser  = dbUser;
        this.dbPass  = dbPass;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, dbUser, dbPass);
    }

    // ── Remote methods ────────────────────────────────────────────────────────

    @Override
    public boolean login(String username, String password) throws RemoteException {
        String sql = "SELECT id FROM accounts WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RemoteException("Error en login: " + e.getMessage(), e);
        }
    }

    @Override
    public double getBalance(String username) throws RemoteException {
        String sql = "SELECT balance FROM accounts WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
            throw new RemoteException("Usuario no encontrado: " + username);
        } catch (SQLException e) {
            throw new RemoteException("Error al consultar saldo: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean withdraw(String username, double amount) throws RemoteException {
        if (amount <= 0) throw new RemoteException("El monto debe ser positivo");

        String checkSql    = "SELECT balance FROM accounts WHERE username = ? FOR UPDATE";
        String withdrawSql = "UPDATE accounts SET balance = balance - ? WHERE username = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setString(1, username);
                ResultSet rs = checkPs.executeQuery();
                if (!rs.next()) {
                    conn.rollback();
                    throw new RemoteException("Usuario no encontrado: " + username);
                }
                double currentBalance = rs.getDouble("balance");
                if (currentBalance < amount) {
                    conn.rollback();
                    return false; // Saldo insuficiente
                }
            }
            try (PreparedStatement updPs = conn.prepareStatement(withdrawSql)) {
                updPs.setDouble(1, amount);
                updPs.setString(2, username);
                updPs.executeUpdate();
            }
            conn.commit();
            System.out.printf("[Retiro] %s -> -%.2f | Nuevo saldo: %.2f%n",
                    username, amount, getBalance(username));
            return true;
        } catch (SQLException e) {
            throw new RemoteException("Error al realizar retiro: " + e.getMessage(), e);
        }
    }
}
