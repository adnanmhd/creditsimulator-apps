# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Install Maven
RUN apk add --no-cache maven bash

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the jar
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy built jar from build stage
COPY --from=build /app/target/credit-simulator-0.0.1.jar credit-simulator.jar

# Setup launcher
RUN mkdir bin
COPY .docker/app.sh bin/credit_simulator
RUN chmod +x bin/credit_simulator
RUN ln -s /app/bin/credit_simulator /app/credit_simulator

# Optional sample input file
COPY file_inputs.txt /app/file_inputs.txt

# Default entrypoint
ENTRYPOINT ["./bin/credit_simulator"]
