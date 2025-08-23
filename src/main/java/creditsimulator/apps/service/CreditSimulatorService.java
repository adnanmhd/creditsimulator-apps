package creditsimulator.apps.service;

import creditsimulator.apps.repository.LoanRepository;
import creditsimulator.apps.dto.Constant.VehicleType;
import creditsimulator.apps.dto.*;
import creditsimulator.apps.exception.DuplicateDataException;
import creditsimulator.apps.util.ApiUtil;
import creditsimulator.apps.util.FileUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreditSimulatorService {
    private static final double BASE_INTEREST_RATE_CAR = 8;
    private static final double BASE_INTEREST_RATE_MOTORCYCLE = 9;
    private static final String LOAD_DATA_URL = "https://dummyjson.com/c/9dc6-2753-42d6-882c";
    private final ApiUtil apiUtil;
    private final LoanRepository loanRepository;


    public CreditSimulatorService(ApiUtil apiUtil, LoanRepository loanRepository) {
        this.apiUtil = apiUtil;
        this.loanRepository = loanRepository;
    }

    public void calculateLoanFromFile(String fileName) throws IOException {
        Map<String, LoanResult> loanResults = new HashMap<>();

        List<Map<String, String>> dataFile = FileUtil.readData(fileName);
        int data = 1;
        for (Map<String, String> eachLine : dataFile) {
            String sheetName = fileName + "-data-" + data;
            LoanResult loanResult = calculateLoan(setLoanRequestFromFile(eachLine));
            loanResults.put(sheetName, loanResult);
            data++;
        }

        loanRepository.saveAll(loanResults);
    }

    private LoanRequest setLoanRequestFromFile(Map<String, String> lineData) {
        LoanRequest request = new LoanRequest();
        Vehicle vehicle = new Vehicle();
        vehicle.setType(lineData.get(FileHeader.VEHICLE_TYPE));
        vehicle.setCondition(lineData.get(FileHeader.VEHICLE_CONDITION));
        vehicle.setYear(lineData.get(FileHeader.VEHICLE_YEAR));
        request.setVehicle(vehicle);
        request.setTenure(Integer.parseInt(lineData.get(FileHeader.LOAN_TENURE)));
        request.setTotalLoanAmount(Double.parseDouble(lineData.get(FileHeader.TOTAL_LOAN_AMOUNT)));
        request.setDownPaymentAmount(Double.parseDouble(lineData.get(FileHeader.DOWN_PAYMENT)));

        return request;
    }

    public LoanResult calculateLoanWithExistingLoad() throws Exception {
        String responseString = apiUtil.callApi("GET", "", LOAD_DATA_URL);
        LoanResponseDto dto = LoanResponseDto.toLoanDto(responseString);

        return calculateLoan(LoanRequest.toLoanRequest(dto));

    }

    public LoanResult calculateLoan(LoanRequest request) {

        double interestRate = getInterestRate(VehicleType.valueOf(request.getVehicle().getType().toUpperCase()));

        LoanService loanService = new LoanService(interestRate, request);

        return loanService.calculateLoan();
    }
    private double getInterestRate(VehicleType vehicleType) {
        return VehicleType.MOBIL.equals(vehicleType) ?
                BASE_INTEREST_RATE_CAR : BASE_INTEREST_RATE_MOTORCYCLE;
    }

    public Map<String, LoanResult> getSavedSheet() {
        return loanRepository.getAllLoan();
    }

    public void saveSheet(String sheetName, LoanResult loan) throws DuplicateDataException {
        loanRepository.saveLoan(sheetName, loan);
    }
}
