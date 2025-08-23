package creditsimulator.apps.controller;

import creditsimulator.apps.dto.Loan;
import creditsimulator.apps.dto.LoanRequest;
import creditsimulator.apps.dto.LoanResult;
import creditsimulator.apps.dto.Vehicle;
import creditsimulator.apps.exception.ApiException;
import creditsimulator.apps.exception.DuplicateDataException;
import creditsimulator.apps.service.CreditSimulatorService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Scanner;

import static creditsimulator.apps.dto.Constant.Condition;
import static creditsimulator.apps.dto.Constant.VehicleType;

public class CreditSimulatorController {
    private final Scanner scanner = new Scanner(System.in);

    private final CreditSimulatorService simulatorService;
    private final InputValidator inputValidator;

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
        this.inputValidator = new InputValidator(scanner);
    }

    public void calculateLoanFromFile(String fileName) {
        try {
            simulatorService.calculateLoanFromFile(fileName);
            System.out.println("Sukses menambahkan data loan dari file " + fileName + ". Silahkan cek di menu Sheet Tersimpan");
            start();
        } catch (IOException e) {
            System.out.println("Terjadi error saat membaca file: " + e.getMessage());
        }
    }

    public void start() {
        try {
            while (true) {
                System.out.println();
                System.out.println("=== Credit Simulator Apps ===");
                System.out.println("1. Buat simulasi baru");
                System.out.println("2. Load Existing Calculation");
                System.out.println("3. Lihat Sheet Tersimpan");
                System.out.println("4. Switch Sheet");
                System.out.println("5. Keluar");

                System.out.print("Pilih menu: ");
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1" -> calculateLoan();
                    case "2" -> calculateLoanWithExistingLoad();
                    case "3" -> showSheets();
                    case "4" -> switchSheet();
                    case "5" -> {
                        System.out.println("Terima kasih");
                        return;
                    }
                    default -> System.out.println("Pilihan tidak valid!");
                }
            }
        } catch (ApiException e) {
            System.out.println("Terjadi kesalahan saat call API - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Terjadi error pada: " + e.getMessage());
        }
    }

    private void showSheets() {
        Map<String, LoanResult> savedLoan = simulatorService.getSavedSheet();

        if (savedLoan.isEmpty()) {
            System.out.println("Belum ada sheet tersimpan.");
        }

        int no = 1;
        System.out.println("=== Daftar Sheet ===");
        for (String sheet : savedLoan.keySet()) {
            System.out.println(no + ". " + sheet);
            no++;
        }
    }

    private void switchSheet() {
        Map<String, LoanResult> savedLoan = simulatorService.getSavedSheet();
        if (savedLoan.isEmpty()) {
            System.out.println("Belum ada sheet tersimpan.");
            return;
        }
        System.out.print("Masukkan nama sheet: ");
        String name = scanner.nextLine().trim();

        if (savedLoan.containsKey(name)) {
            printCalculationResult(savedLoan.get(name));
        } else {
            System.out.println("Sheet tidak ditemukan!");
        }
    }

    public void calculateLoanWithExistingLoad() throws Exception {
        LoanResult result = simulatorService.calculateLoanWithExistingLoad();
        printCalculationResult(result);
        saveSheet(result);
    }

    public void calculateLoan() {
        System.out.println("=== Simulasi Baru ===");

        vehicleType = inputValidator.validateInput(
                "Jenis Kendaraan Motor|Mobil: ",
                input -> input.equalsIgnoreCase(VehicleType.MOTOR.toString())
                        || input.equalsIgnoreCase(VehicleType.MOBIL.toString()),
                "Invalid input! Mohon input 'Motor' atau 'Mobil': "
        );

        vehicleCondition = inputValidator.validateInput(
                "Kendaraan Bekas|Baru: ",
                input -> input.equalsIgnoreCase(Condition.BEKAS.toString())
                        || input.equalsIgnoreCase(Condition.BARU.toString()),
                "Invalid input! Mohon input 'Bekas' atau 'Baru': "
        );

        yearOfVehicle = inputValidator.validateYear(vehicleCondition);

        loanAmount = inputValidator.validateInput(
                "Jumlah Pinjaman Total: ",
                input -> input.matches("\\d+"),
                "Invalid input! Mohon masukkan angka: "
        );

        loanTenure = inputValidator.validateInput(
                "Tenor Pinjaman 1-6 thn: ",
                input -> input.matches("[1-6]"),
                "Invalid input! Masukan angka 1-6: "
        );

        dpAmount = inputValidator.validateDP(loanAmount, vehicleCondition);

        setVehicleAndLoan();

        LoanResult result = simulatorService.calculateLoan(loan);
        printCalculationResult(result);
        saveSheet(result);
    }

    private void saveSheet(LoanResult result) {
        while (true) {
            System.out.print("Masukan nama sheet: ");
            String sheetName = scanner.nextLine().trim();

            try {
                simulatorService.saveSheet(sheetName, result);
                System.out.println("Sheet berhasil disimpan dengan nama: " + sheetName);
                break;
            } catch (DuplicateDataException e) {
                System.out.println("Sheet dengan nama '" + sheetName + "' sudah ada. Silakan masukan nama lain.");
            } catch (Exception e) {
                System.out.println("Terjadi kesalahan saat menyimpan: " + e.getMessage());
                break;
            }
        }
    }

    private void printCalculationResult(LoanResult result) {
        for (Loan data : result.getLoanResultCalculation()) {
            System.out.println("Tahun: " + data.getCurrentYear());
            System.out.println("Pokok Pinjaman: " + BigDecimal.valueOf(data.getLoanPrincipal()).toPlainString());
            System.out.println("Rate: " + data.getInterestRate());
            System.out.println("Total Pinjaman: " + BigDecimal.valueOf(data.getLoanTotal()).toPlainString());
            System.out.println("Installment Monthly: " + BigDecimal.valueOf(data.getInstallmentMonthly()).toPlainString());
            System.out.println("Installment Yearly: " + BigDecimal.valueOf(data.getInstallmentYearly()).toPlainString());
            System.out.println();
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
