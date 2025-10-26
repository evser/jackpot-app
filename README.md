# Jackpot Backend Service

This is a Spring Boot application that manages jackpot contributions and rewards based on user bets. It provides API endpoints to
submit bets and evaluate them for wins, using Kafka for asynchronous processing and H2 (in-memory) for data storage.

**API Endpoints:**

* `POST /api/bets`: Submits a bet for processing.
* `POST /api/bets/{betId}/evaluate`: Evaluates if a submitted bet won the jackpot.

## How to Run

### Tech Stack

* Java 21
* Spring Boot 3.2.x
* (Optional) Docker Desktop with Kafka running (for non-mocked mode).

---

### Option 1: Run with Mock Kafka (Default)

This is the simplest way to run the application. It bypasses Kafka entirely and processes bets synchronously in memory.
**Requires Java 21.**

1. **Set Profile:**
   Ensure `spring.profiles.active=mock-kafka` is set in `src/main/resources/application.properties`. This is the default.

2. **Run the application:**
   (Using Wrapper)
   ```bash
   # On macOS/Linux
   # Make the wrapper executable (only need to do this once)
    chmod +x ./gradlew
   # This will build and run the application.
   # It uses the 'kafka' profile by default in application.properties
    ./gradlew bootRun
   
   # On Windows
    .\gradlew bootRun
   ```

The application will be available at `http://localhost:8080`.

---

### Option 2: Run with Real Kafka

This mode requires a running Kafka broker.

1. **Start Kafka and the application:**
   Use the pre-configured setting from `docker-compose.yml` and run `docker-compose up -d`.

2. Wait until after the first run docker pulls the images.

The application will be available at `http://localhost:8080`.

---

## How to Use

### 1. Access H2 Console

You can inspect the in-memory database to see jackpots, contributions, and rewards.

* **URL:** `http://localhost:8080/h2-console`
* **JDBC URL:** `jdbc:h2:mem:jackpotdb`
* **Username:** `sa`
* **Password:** `password`

Click "Connect" and run queries, e.g., `SELECT * FROM jackpots` or `SELECT * FROM jackpot_contributions`.

### Pre-populated Data for testing purposes

The `DatabaseLoader` pre-populates the H2 database with three jackpots. Use their `id`s in your API requests.

**Jackpot 1**

* `Initial Pool`: 10 000
* `Contribution`: **Fixed** at 5% of bet amount.
* `Reward`: **Variable**, starts at 0.1%, 100% win if pool hits 1 000 000

**Jackpot 2**

* `Initial Pool`: 1 000
* `Contribution`: **Variable** starts at 10%, scales down to 2% as pool grows to 10 000.
* `Reward`: **Fixed** 1% chance.

### 2. API Endpoints

### Publish a Bet

Submits a bet to the `jackpot-bets` Kafka topic for processing.

**Request:**

```
POST /api/bets
```

**Body:**

```
{
    "betId": 12345,
    "userId": 67890,
    "jackpotId": 1,
    "betAmount": 100.00
}
```

**Response:**

* `202 ACCEPTED`: If the bet is successfully sent to Kafka.
* `400 BAD REQUEST`: If the request body is invalid.

### 2. Evaluate a Bet for a Win

Checks if a previously contributed bet has won the jackpot. This endpoint is idempotent *if the bet did not win*. If the bet
*wins*, the jackpot pool is reset.

**Request:**

```
POST /api/bets/{betId}/evaluate
```

**Example:**

```
POST http://localhost:8080/api/bets/12345/evaluate
```

**Response _Jackpot_:** `200 OK`

```
{
    "isJackpot": true,
    "rewardAmount": 1050.00,
}
```

**Response _No Jackpot_:** `200 OK`

```
{
    "isJackpot": false,
    "rewardAmount": 0.00,
}

```

**Response _Not Found_:** `404 NOT FOUND`

```
{
    "errorMessage": "Bet contribution not found."
}
```
