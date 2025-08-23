package creditsimulator.apps.service;

import creditsimulator.apps.repository.LoanRepository;
import creditsimulator.apps.dto.*;
import creditsimulator.apps.exception.DuplicateDataException;
import creditsimulator.apps.util.ApiUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreditSimulatorServiceTest {

    private ApiUtil apiUtil;
    private LoanRepository loanRepository;
    private CreditSimulatorService simulatorService;

    @BeforeEach
    void setUp() {
        apiUtil = mock(ApiUtil.class);
        loanRepository = mock(LoanRepository.class);
        simulatorService = new CreditSimulatorService(apiUtil, loanRepository);
    }

    @Test
    void testCalculateLoan() {
        Vehicle vehicle = new Vehicle();
        vehicle.setType("Mobil");
        vehicle.setCondition("Baru");

        LoanRequest request = new LoanRequest();
        request.setVehicle(vehicle);
        request.setTotalLoanAmount(1_000_000_000.0);
        request.setDownPaymentAmount(500_000_000.0);
        request.setTenure(2);

        LoanResult result = simulatorService.calculateLoan(request);

        assertNotNull(result);
        assertEquals(2, result.getTenure());
        assertEquals(1_000_000_000.0, result.getTotalLoanAmount());
        assertEquals(500_000_000.0, result.getDownPaymentAmount());
        assertNotNull(result.getLoanResultCalculation());
        assertFalse(result.getLoanResultCalculation().isEmpty());
    }

    @Test
    void testSaveSheet_duplicate() throws DuplicateDataException {
        LoanResult dummyResult = new LoanResult();
        String key = "sheet1";

        doThrow(new DuplicateDataException("loan data already exist"))
                .when(loanRepository).saveLoan(eq(key), any(LoanResult.class));

        assertThrows(DuplicateDataException.class, () -> simulatorService.saveSheet(key, dummyResult));
    }

    @Test
    void testGetSavedSheet() {
        Map<String, LoanResult> mockData = new HashMap<>();
        mockData.put("sheet1", new LoanResult());

        when(loanRepository.getAllLoan()).thenReturn(mockData);

        Map<String, LoanResult> result = simulatorService.getSavedSheet();
        assertEquals(1, result.size());
        assertTrue(result.containsKey("sheet1"));
    }

    @Test
    void testCalculateLoanWithExistingLoad() throws Exception {
        String dummyResponse = "{ \"vehicleType\": \"Mobil\", \"vehicleCondition\": \"Baru\", \"vehicleYear\": 2025, " +
                "\"totalLoanAmount\": 1000000000, \"loanTenure\": 6, \"downPayment\": 500000000 }";

        when(apiUtil.callApi(eq("GET"), eq(""), anyString())).thenReturn(dummyResponse);

        LoanResult result = simulatorService.calculateLoanWithExistingLoad();

        assertNotNull(result);
        assertEquals(6, result.getTenure());
        assertEquals(1000000000, result.getTotalLoanAmount());
    }

    @Test
    void testCalculateLoanFromFile() throws IOException {
        File tempFile = File.createTempFile("test_input", ".txt");
        tempFile.deleteOnExit();

        String content = String.join("\n",
                "vehicle_type|vehicle_condition|vehicle_year|total_loan_amount|loan_tenure|down_payment",
                "Mobil|Baru|2025|1000000000|6|500000000"
        );

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(content);
        }

        simulatorService.calculateLoanFromFile(tempFile.getAbsolutePath());

        verify(loanRepository, times(1)).saveAll(Mockito.anyMap());
    }

    @Test
    void testCalculateLoanFromFile_multipleRows() throws IOException {
        File tempFile = File.createTempFile("test_input_multi", ".txt");
        tempFile.deleteOnExit();

        String content = String.join("\n",
                "vehicle_type|vehicle_condition|vehicle_year|total_loan_amount|loan_tenure|down_payment",
                "Mobil|Baru|2025|1000000000|6|500000000",
                "Motor|Bekas|2020|50000000|3|10000000"
        );

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(content);
        }

        simulatorService.calculateLoanFromFile(tempFile.getAbsolutePath());

        verify(loanRepository, times(1)).saveAll(Mockito.anyMap());
    }
}
