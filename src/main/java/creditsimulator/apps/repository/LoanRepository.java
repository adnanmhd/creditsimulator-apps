package creditsimulator.apps.repository;

import creditsimulator.apps.dto.LoanResult;
import creditsimulator.apps.exception.DuplicateDataException;

import java.util.HashMap;
import java.util.Map;

public class LoanRepository {
    private final Map<String, LoanResult> savedLoan = new HashMap<>();

    public void saveLoan(String key, LoanResult result) throws DuplicateDataException {
        if (savedLoan.containsKey(key)) {
            throw new DuplicateDataException("loan data already exist");
        }
        savedLoan.put(key, result);
    }

    public void saveAll(Map<String, LoanResult> loans) {
        savedLoan.putAll(loans);
    }

    public Map<String, LoanResult> getAllLoan() {
        return savedLoan;
    }
}
