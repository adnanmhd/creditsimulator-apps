# Credit Simulator Apps
A simple Java console application to simulate credit/loan calculations.

---

# Features:
1. [x] Buat Simulasi Baru
2. [x] Load Existing Calculation
3. [x] Lihat Sheet Tersimpan
4. [x] Switch Sheet



## Requirements

- Docker Desktop installed
- (Optional) Java 21 if running locally without Docker

---

## How to Run with Docker

The application is packaged as a Docker image on Docker Hub. You don’t need to set up Java or Maven locally.
And also, in this image there already available a file 'file_inputs.txt' to test the application

### Pull the Docker Image

```bash
docker pull madnan19/creditsimulator-apps:latest

```

### Run The Application

```bash
bin/credit_simulator file_inputs.txt 
-- or 
./credit_simulator file_inputs.txt

-- execute without file:

bin/credit_simulator
-- or 
./credit_simulator

```

## Run Locally without Docker

```bash
java -jar credit-simulator-0.0.1.jar file_inputs.txt

-- run without file_inputs

java -jar credit-simulator-0.0.1.jar
```

### Input File Format 
```file 
vehicle_type|vehicle_condition|vehicle_year|total_loan_amount|loan_tenure|down_payment
Mobil|Baru|2025|1000000000|6|500000000
Motor|Bekas|2021|1000000000|3|500000000
```

## Perform Unit Test

```maven
mvn clean test
```

## Others

This apps also has function `Load Existing Calculation` to call external API to perform loan calculation base on the API response.
URL:
[dummyjson.com/c/9dc6-2753-42d6-882c]()

Response sample:

```file
{
  "vehicleType": "Mobil",
  "vehicleCondition": "Baru",
  "vehicleYear": 2025,
  "totalLoanAmount": 1000000000,
  "loanTenure": 6,
  "downPayment": 500000000
}
```

