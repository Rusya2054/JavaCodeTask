# JavaCodeTask


## Microservice for managing wallet transactions: deposits, withdrawals and balance checks.

### Tools:
- Java 17
- Spring Boot 3
- PostgreSQL 16
- Liquibase (db migration)
- Docker + Docker Compose
- Maven

## API Endpoints
### Deposit/withdrawal of funds
**POST** `/api/v1/wallet`
```json
{
  "walletId": "UUID",
  "operationType": "DEPOSIT/WITHDRAW",
  "amount": 1000
}
```
### Get information about wallet balance with UUID
**GET** `/api/v1/wallet/UUID`

### Environments:
- `DB_HOST`: database host (default: `postgres`)
- `DB_PORT`: database port (default: `5432`)
- `DB_NAME`: database name (default: `postgres`)
- `DB_USERNAME`: the name of the user to connect to the database (default: `postgres`)
- `DB_PASSWORD`: the user's password for accessing the database (default: `postgres`)
- `SERVER_PORT`: port of working servir application (default: `8181`)
- `OUTPUT_PORT`: port of server application for mapping (default: `8181`)
- `NUMBER_OUTPUT_SCALE`: Number of decimal places in the output (default: `3`)

### Building
#### Docker compose
- run ```docker compose up --build```
#### Java Running
1) enter to java_app directory
2) run for building ```mvn package``` or ```mvn clean package```
- run for tests ```mvn test``` or ```mvn clean test```
#### Optional
You may define ```.env``` file with parameters in [Environments](#environments)
