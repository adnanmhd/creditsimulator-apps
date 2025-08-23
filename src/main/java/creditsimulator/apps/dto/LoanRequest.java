package creditsimulator.apps.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanRequest {
    private Vehicle vehicle;
    private Double totalLoanAmount;
    private int tenure;
    private Double downPaymentAmount;
}
