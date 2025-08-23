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

    public static LoanRequest toLoanRequest(LoanResponseDto api) {
        LoanRequest request = new LoanRequest();
        Vehicle vehicle = new Vehicle();
        vehicle.setType(api.getVehicleType());
        vehicle.setYear(api.getVehicleYear());
        vehicle.setCondition(api.getVehicleCondition());

        request.setVehicle(vehicle);
        request.setTotalLoanAmount(Double.parseDouble(api.getTotalLoanAmount()));
        request.setTenure(Integer.parseInt(api.getLoanTenure()));
        request.setDownPaymentAmount(Double.parseDouble(api.getDownPayment()));

        return request;
    }
}
