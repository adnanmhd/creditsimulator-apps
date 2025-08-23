package creditsimulator.apps.controller;

import creditsimulator.apps.dto.Constant;

import java.time.Year;
import java.util.Scanner;
import java.util.function.Predicate;

public class InputValidator {
    private final Scanner scanner;

    public InputValidator(Scanner scanner) {
        this.scanner = scanner;
    }

    public String validateInput(String message, Predicate<String> validator, String errorMessage) {
        System.out.print(message);
        while (true) {
            String input = scanner.nextLine().trim();
            if (validator.test(input)) {
                return input;
            }
            System.out.print(errorMessage);
        }
    }

    public String validateYear(String vehicleCondition) {
        int currentYear = Year.now().getValue();
        System.out.print("Tahun Kendaraan: ");
        while (true) {
            String input = scanner.nextLine().trim();
            if (!input.matches("\\d{4}")) {
                System.out.print("Invalid input! Mohon input 4 digit angka tahun: ");
                continue;
            }
            int year = Integer.parseInt(input);
            if (Constant.Condition.BARU.toString().equalsIgnoreCase(vehicleCondition)
                    && year < currentYear-1) {

                System.out.print("Tahun kendaraan baru tidak valid: ");

            } else {
                return input;
            }
        }
    }

    public String validateDP(String loanAmount, String vehicleCondition) {
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

            if (Constant.Condition.BEKAS.toString().equalsIgnoreCase(vehicleCondition)
                    && dp < validDpForUsed) {

                System.out.printf(errorMessage, "25%");

            } else if (Constant.Condition.BARU.toString().equalsIgnoreCase(vehicleCondition)
                    && dp < validDpForNew) {
                System.out.printf(errorMessage, "35%");
            } else {
                return input;
            }
        }
    }
}
