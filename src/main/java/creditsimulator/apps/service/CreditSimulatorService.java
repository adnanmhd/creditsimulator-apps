package creditsimulator.apps.service;

import creditsimulator.apps.LoanRepository;
import creditsimulator.apps.dto.Constant.VehicleType;
import creditsimulator.apps.dto.Loan;
import creditsimulator.apps.dto.LoanRequest;
import creditsimulator.apps.dto.LoanResponseDto;
import creditsimulator.apps.dto.LoanResult;
import creditsimulator.apps.exception.DuplicateDataException;
import creditsimulator.apps.util.ApiUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreditSimulatorService {
    private static final double BASE_INTEREST_RATE_CAR = 8;
    private static final double BASE_INTEREST_RATE_MOTORCYCLE = 9;

    private static final String LOAD_DATA_URL = "https://dummyjson.com/c/9dc6-2753-42d6-882c";
    private double interestRate;

    private final ApiUtil apiUtil;

    private final LoanRepository loanRepository;


    public CreditSimulatorService(ApiUtil apiUtil, LoanRepository loanRepository) {
        this.apiUtil = apiUtil;
        this.loanRepository = loanRepository;
    }

    public LoanResult calculateLoanWithExistingLoad() throws Exception {
        String responseString = apiUtil.callApi("GET", "", LOAD_DATA_URL);
        LoanResponseDto dto = LoanResponseDto.toLoanDto(responseString);

        return calculateLoan(LoanRequest.toLoanRequest(dto));

    }

    public LoanResult calculateLoan(LoanRequest request) {
        LoanResult loanResult = new LoanResult();

        setInterestRate(VehicleType.valueOf(request.getVehicle().getType().toUpperCase()));

        double loanPrincipal = request.getTotalLoanAmount() - request.getDownPaymentAmount();
        double loanTotal = 0;
        double installmentMonthlyTotal = 0;
        double installmentYearly = 0;
        double installmentMonthly;
        List<Loan> loanData = new ArrayList<>();

        for (int year = 0; year < request.getTenure(); year++) {
            Loan loan = new Loan();
            loan.setCurrentYear(year+1);
            int monthlyDecreaseValue = 12 * year;
            if (year > 0) {
                increaseYearlyInterest(year);
                loanPrincipal = loanTotal - installmentYearly;
            }
            loanTotal = (loanPrincipal * interestRate / 100) + loanPrincipal;
            installmentMonthly = loanTotal / ((12 * request.getTenure()) - monthlyDecreaseValue);
            installmentYearly = installmentMonthly * 12;
            installmentMonthlyTotal = installmentMonthlyTotal +
                    (installmentYearly/12);

            loan.setInterestRate(interestRate);
            loan.setLoanPrincipal(loanPrincipal);
            loan.setLoanTotal(loanTotal);
            loan.setInstallmentMonthly(installmentMonthly);
            loan.setInstallmentYearly(installmentYearly);

            loanData.add(loan);
        }

        setLoanResult(loanResult, request, loanData);
        return loanResult;
    }
    private void setInterestRate(VehicleType vehicleType) {
        interestRate = VehicleType.MOBIL.equals(vehicleType) ?
                BASE_INTEREST_RATE_CAR : BASE_INTEREST_RATE_MOTORCYCLE;
    }

    private void setLoanResult(LoanResult loanResult, LoanRequest request, List<Loan> loanData) {
        loanResult.setDownPaymentAmount(request.getDownPaymentAmount());
        loanResult.setTenure(request.getTenure());
        loanResult.setTotalLoanAmount(request.getTotalLoanAmount());
        loanResult.setVehicle(request.getVehicle());
        loanResult.setLoanResultCalculation(loanData);
    }

    private void increaseYearlyInterest(int year) {
        if(year%2 == 1) {
            interestRate = interestRate + 0.1;
        } else {
            interestRate = interestRate + 0.5;
        }
    }

    public Map<String, LoanResult> getSavedSheet() {
        return loanRepository.getAllLoan();
    }

    public void saveSheet(String sheetName, LoanResult loan) throws DuplicateDataException {
        loanRepository.saveLoan(sheetName, loan);
    }
}
