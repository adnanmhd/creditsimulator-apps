package creditsimulator.apps.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanResponseDto {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String vehicleType;
    private String vehicleCondition;
    private String vehicleYear;
    private String totalLoanAmount;
    private String loanTenure;
    private String downPayment;

    public static LoanResponseDto toLoanDto(String json) throws Exception {
        return objectMapper.readValue(json, LoanResponseDto.class);
    }
}
