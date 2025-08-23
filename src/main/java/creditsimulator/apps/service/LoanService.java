package creditsimulator.apps.service;

import creditsimulator.apps.dto.Loan;
import creditsimulator.apps.dto.LoanRequest;
import creditsimulator.apps.dto.LoanResult;

import java.util.ArrayList;
import java.util.List;

public class LoanService {
    private double interestRate;
    private final LoanRequest request;

    public LoanService(double interestRate, LoanRequest loanRequest) {
        this.interestRate = interestRate;
        this.request = loanRequest;
    }

    public LoanResult calculateLoan() {
        LoanResult loanResult = new LoanResult();

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
}
