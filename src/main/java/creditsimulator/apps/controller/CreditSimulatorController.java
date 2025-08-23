package creditsimulator.apps.controller;

import creditsimulator.apps.dto.Loan;
import creditsimulator.apps.dto.LoanRequest;
import creditsimulator.apps.dto.LoanResult;
import creditsimulator.apps.dto.Vehicle;
import creditsimulator.apps.service.CreditSimulatorService;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Scanner;
import java.util.function.Predicate;

import static creditsimulator.apps.dto.Constant.Condition;
import static creditsimulator.apps.dto.Constant.VehicleType;

public class CreditSimulatorController {
    private final Scanner scanner = new Scanner(System.in);

    private final CreditSimulatorService simulatorService;
    String vehicleType;
    String vehicleCondition;
    String yearOfVehicle;
    String loanAmount;
    String loanTenure;
    String dpAmount;
    Vehicle vehicle;
    LoanRequest loan;

    public CreditSimulatorController(CreditSimulatorService simulatorService) {
        this.simulatorService = simulatorService;
    }

    public void start() {
        System.out.println("=== Credit Simulator Apps ===");

        vehicleType = validateInput(
                "Jenis Kendaraan Motor|Mobil: ",
                input -> input.equalsIgnoreCase(VehicleType.MOTOR.toString())
                        || input.equalsIgnoreCase(VehicleType.MOBIL.toString()),
                "Invalid input! Mohon input 'Motor' atau 'Mobil': "
        );

        vehicleCondition = validateInput(
                "Kendaraan Bekas|Baru: ",
                input -> input.equalsIgnoreCase(Condition.BEKAS.toString())
                        || input.equalsIgnoreCase(Condition.BARU.toString()),
                "Invalid input! Mohon input 'Bekas' atau 'Baru': "
        );

        yearOfVehicle = validateYear();

        loanAmount = validateInput(
                "Jumlah Pinjaman Total: ",
                input -> input.matches("\\d+"),
                "Invalid input! Mohon masukkan angka: "
        );

        loanTenure = validateInput(
                "Tenor Pinjaman 1-6 thn: ",
                input -> input.matches("[1-6]"),
                "Invalid input! Masukan angka 1-6: "
        );

        dpAmount = validateDP();

        setVehicleAndLoan();

        LoanResult result = simulatorService.calculateLoan(loan);

        for (Loan data : result.getLoanResultCalculation()) {
            System.out.println("tahun: " + data.getCurrentYear());
            System.out.println("pokok pinjaman: " + BigDecimal.valueOf(data.getLoanPrincipal()).toPlainString());
            System.out.println("rate: " + data.getInterestRate());
            System.out.println("total pinjaman: " + BigDecimal.valueOf(data.getLoanTotal()).toPlainString());
            System.out.println("installment monthly: " + BigDecimal.valueOf(data.getInstallmentMonthly()).toPlainString());
            System.out.println("installment yearly: " + BigDecimal.valueOf(data.getInstallmentYearly()).toPlainString());
            System.out.println();
        }

    }
    private String validateInput(String message, Predicate<String> validator, String errorMessage) {
        System.out.print(message);
        while (true) {
            String input = scanner.nextLine().trim();
            if (validator.test(input)) {
                return input;
            }
            System.out.print(errorMessage);
        }
    }

    private String validateYear() {
        int currentYear = Year.now().getValue();
        System.out.print("Tahun Kendaraan: ");
        while (true) {
            String input = scanner.nextLine().trim();
            if (!input.matches("\\d{4}")) {
                System.out.print("Invalid input! Mohon input 4 digit angka tahun: ");
                continue;
            }
            int year = Integer.parseInt(input);
            if (Condition.BARU.toString().equalsIgnoreCase(vehicleCondition)
                    && year < currentYear-1) {

                System.out.print("Tahun kendaraan baru tidak valid: ");

            } else {
                return input;
            }
        }
    }

    private String validateDP() {
        String errorMessage = "Nilai DP minimal %s dari pinjaman: ";
        double totalLoan = Double.parseDouble(loanAmount);
        double validDpForUsed = totalLoan * 25 / 100;
        double validDpForNew = totalLoan * 35 / 100;

        System.out.print("Jumlah DP: ");
        while (true) {
            String input = scanner.nextLine().trim();
            if (!input.matches("\\d+")) {
                System.out.print("Invalid input! Mohon masukkan angka: ");
                continue;
            }
            double dp = Double.parseDouble(input);

            if (Condition.BEKAS.toString().equalsIgnoreCase(vehicleCondition)
                    && dp < validDpForUsed) {

                System.out.printf(errorMessage, "25%");

            } else if (Condition.BARU.toString().equalsIgnoreCase(vehicleCondition)
                    && dp < validDpForNew) {
                System.out.printf(errorMessage, "35%");
            } else {
                return input;
            }
        }
    }

    void setVehicleAndLoan() {
        vehicle = new Vehicle();
        vehicle.setType(vehicleType);
        vehicle.setCondition(vehicleCondition);
        vehicle.setYear(yearOfVehicle);

        loan = new LoanRequest();
        loan.setVehicle(vehicle);
        loan.setTotalLoanAmount(Double.valueOf(loanAmount));
        loan.setTenure(Integer.parseInt(loanTenure));
        loan.setDownPaymentAmount(Double.valueOf(dpAmount));
    }

}
