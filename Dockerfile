# --- Stage 1: Build stage using Maven & JDK 21 ---
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the executable fat JAR (skipping tests for speed)
RUN mvn clean package -DskipTests

# --- Stage 2: Minimal runtime environment ---
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built executable fat JAR from the builder stage
COPY --from=builder /app/target/Tic-Tac-Toe-1.0-SNAPSHOT.jar tictactoe.jar

# Run the game
ENTRYPOINT ["java", "-jar", "tictactoe.jar"]
