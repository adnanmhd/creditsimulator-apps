package creditsimulator.apps.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoanResult extends LoanRequest{
    private List<Loan> loanResultCalculation;
}
