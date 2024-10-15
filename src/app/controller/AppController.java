package app.controller;

import app.model.users.User;
import app.service.AppointmentService;
import app.service.CsvReaderService;
import app.service.InventoryService;
import app.service.UserService;
import java.io.IOException;

public class AppController {

	public static final String STAFF_FILEPATH = "src/resources/Staff_List.csv";
	public static final String PATIENT_FILEPATH = "src/resources/Patient_List.csv";
	public static final String INVENTORY_FILEPATH = "src/resources/Medicine_List.csv";

	private UserService userService;
	private InventoryService inventoryService;
	private AppointmentService appointmentService;
	private CsvReaderService csvReaderService;
	// private User currentUser; // IMPT this might not be able to downcast
	
	public AppController() {
		userService = new UserService();
		inventoryService = new InventoryService();
		appointmentService = new AppointmentService();
		csvReaderService = new CsvReaderService();
	}

	public void loadData() {

		try {
			userService.loadPatients(csvReaderService.loadData(PATIENT_FILEPATH));
			userService.loadStaff(csvReaderService.loadData(STAFF_FILEPATH));
			// inventoryService.addMedicine(csvReaderService.read(INVENTORY_FILEPATH));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void login(String id, String pw) {
		User currentUser = userService.findUser(id, pw);
		if(currentUser != null) {
			currentUser.displayUserMenu(); // TODO CONVERT THESE TO THEIR OWN CONTROLLERS?
		}
	}

	public void start() {
		// display login
		// login user
		// loop (logout, login)
	}
}