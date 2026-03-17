# 🏦 Banco RMI – Java RMI + MySQL + Docker Compose

Aplicación bancaria de ejemplo con arquitectura cliente-servidor basada en **Java RMI**, GUI **Swing** y base de datos **MySQL**, todo orquestado con **Docker Compose**.

## Estructura del proyecto

```
rmi/
├── db/
│   └── init.sql            # Schema + usuarios mock
├── server/                 # Servidor RMI
│   ├── src/main/java/com/bank/
│   │   ├── BankService.java        # Interfaz remota
│   │   ├── BankServiceImpl.java    # Implementación JDBC
│   │   └── BankServer.java         # Lanzador del servidor
│   ├── pom.xml
│   └── Dockerfile
├── client/                 # Cliente Swing
│   ├── src/main/java/com/bank/
│   │   ├── BankService.java        # Copia de la interfaz
│   │   └── client/
│   │       ├── BankClient.java     # Entry point
│   │       ├── LoginScreen.java    # Pantalla 1 – Login
│   │       ├── MenuScreen.java     # Pantalla 2 – Menú
│   │       ├── BalanceScreen.java  # Pantalla 3 – Saldo
│   │       └── WithdrawScreen.java # Pantalla 4 – Retiro
│   ├── pom.xml
│   └── Dockerfile
└── docker-compose.yml
```

## Usuarios mock (pre-registrados)

| Usuario | Contraseña | Saldo inicial |
|---------|-----------|---------------|
| alice   | 1234      | $5,000.00     |
| bob     | 1234      | $3,200.00     |
| carlos  | 1234      | $8,750.50     |

## Cómo ejecutar

### Opción A – Servidor + DB en Docker, cliente local (recomendado para macOS)

```bash
# 1. Levantar base de datos y servidor RMI
docker-compose up --build db server

# 2. Compilar y correr el cliente localmente
cd client
mvn package -q
java -DRMI_HOST=localhost -jar target/bank-client.jar
```

> Si `RMI_HOST` no funciona como propiedad de sistema, editar `BankClient.java` 
> para hardcodear `"localhost"` o usar la variable de entorno:
> ```bash
> RMI_HOST=localhost java -jar target/bank-client.jar
> ```

### Opción B – Todo en Docker (requiere X11 / XQuartz en macOS)

```bash
# macOS: instalar XQuartz y habilitarlo
brew install --cask xquartz
open -a XQuartz
# En preferencias de XQuartz: habilitar "Allow connections from network clients"

# Permitir conexiones locales
xhost + 127.0.0.1

# Levantar todos los servicios
DISPLAY=host.docker.internal:0 docker-compose up --build
```

### Verificar la base de datos

```bash
# Ver usuarios y saldos
docker-compose exec db mysql -ubankuser -pbankpass bank \
  -e "SELECT username, balance FROM accounts;"
```

## Notas de arquitectura

- El servidor espera a MySQL con un **retry loop** (30 intentos × 2s).
- El retiro usa una **transacción con `FOR UPDATE`** para evitar race conditions.
- El cliente reconecta al servidor con **retry loop** (20 intentos × 3s).
- Las credenciales de BD se pasan via **variables de entorno** en docker-compose.
