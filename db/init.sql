-- init.sql – Se ejecuta automaticamente la primera vez que MySQL inicia
CREATE DATABASE IF NOT EXISTS bank;
USE bank;

CREATE TABLE IF NOT EXISTS accounts (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)    UNIQUE NOT NULL,
    password VARCHAR(100)   NOT NULL,
    balance  DECIMAL(12, 2) NOT NULL DEFAULT 0.00
);

-- Usuarios mock con saldos iniciales
INSERT INTO accounts (username, password, balance) VALUES
    ('alice',  '1234', 5000.00),
    ('bob',    '1234', 3200.00),
    ('carlos', '1234', 8750.50)
ON DUPLICATE KEY UPDATE balance = VALUES(balance);
