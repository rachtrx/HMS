package app.controller;

import app.constants.exceptions.ExitApplication;
import app.model.appointments.AppointmentService;
import app.model.users.User;
import app.service.CsvReaderService;
import app.service.InventoryService;
import app.service.MenuService;
import app.service.UserService;
import java.io.IOException;
import java.util.Scanner;

public class AppController {

	public static final String STAFF_FILEPATH = "src/resources/Staff_List.csv";
	public static final String PATIENT_FILEPATH = "src/resources/Patient_List.csv";
	public static final String INVENTORY_FILEPATH = "src/resources/Medicine_List.csv";

	private static User currentUser;

	private UserService userService;
	private InventoryService inventoryService;
	private AppointmentService appointmentService;
	private CsvReaderService csvReaderService;
	private MenuService menuService;
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

	public void login(String id, String password) {
		User currentUser = userService.findUser(id, password);
		if(currentUser != null) {
			menuService.setCurrentMenu(currentUser); // TODO CONVERT THESE TO THEIR OWN CONTROLLERS?
		}
	}

	public void logout() {

	}

	public void start() {
		// display login
		// login user
		// loop (logout, login)
		Scanner scanner = new Scanner(System.in);
		MenuService menuService = new MenuService();
		while (true) {
			try {
				menuService.getCurrentMenu().display();
				menuService.handleUserInput(scanner.nextLine());
			} catch (ExitApplication e) {
				System.out.println(e.getMessage());
			}
		}
		scanner.close();
	}

	private static void setCurrentUser(User user) {
		AppController.currentUser = user;
	}

	public static User getCurrentUser() {
		return AppController.currentUser;
	}
}