package creditsimulator.apps;


import creditsimulator.apps.controller.CreditSimulatorController;
import creditsimulator.apps.service.CreditSimulatorService;

public class AppsApplication {

	public static void main(String[] args) {

		CreditSimulatorService creditSimulatorService = new CreditSimulatorService();
		CreditSimulatorController controller = new CreditSimulatorController(creditSimulatorService);
		controller.start();

	}

}
