package creditsimulator.apps;


import creditsimulator.apps.controller.CreditSimulatorController;
import creditsimulator.apps.repository.LoanRepository;
import creditsimulator.apps.service.CreditSimulatorService;
import creditsimulator.apps.util.ApiUtil;

public class AppsApplication {

	public static void main(String[] args) {

		LoanRepository loanRepository = new LoanRepository();
		CreditSimulatorService creditSimulatorService = new CreditSimulatorService(new ApiUtil(), loanRepository);
		CreditSimulatorController controller = new CreditSimulatorController(creditSimulatorService);

		if (args.length >= 1) {
			String fileName = args[0];
			controller.calculateLoanFromFile(fileName);
		} else {
			controller.start();
		}
	}


}
