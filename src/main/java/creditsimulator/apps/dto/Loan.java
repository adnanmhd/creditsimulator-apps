package creditsimulator.apps.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Loan {
    private int currentYear;
    private Double interestRate;
    private Double loanPrincipal;
    private Double loanTotal;
    private Double installmentYearly;
    private Double installmentMonthly;
}
