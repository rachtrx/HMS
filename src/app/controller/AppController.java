package app.controller;

import app.service.CsvReaderService;
import app.service.InventoryService;
import app.service.MenuService;
import app.service.UserService;

public class AppController {

	public static final String STAFF_FILEPATH = "src/resources/Staff_List.csv";
	public static final String PATIENT_FILEPATH = "src/resources/Patient_List.csv";
	public static final String INVENTORY_FILEPATH = "src/resources/Medicine_List.csv";

	private static final UserService userService = new UserService();
	private static final InventoryService inventoryService = new InventoryService();
	// private AppointmentService appointmentService = new AppointmentService();
	private static final CsvReaderService csvReaderService = new CsvReaderService();
	private static final MenuService menuService = new MenuService();

	public static UserService getUserService() {
		return AppController.userService;
	}
	
	public static InventoryService getInventoryService() {
		return AppController.inventoryService;
	}

	public static CsvReaderService getCsvReaderService() {
		return AppController.csvReaderService;
	}
	
	public static void handleUserInput(String userInput) throws Exception {
		AppController.menuService.next(userInput.trim().toLowerCase());
	}
}