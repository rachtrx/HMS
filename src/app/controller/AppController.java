package app.controller;

import app.db.ACsvReaderService;
import app.model.user_input.menus_test.TestMenuService;
import app.model.users.User;
import app.service.InventoryService;
import app.service.UserService;
import java.io.IOException;

public class AppController {

	public static final String USER_FILEPATH = "src/resources/User_List.csv";
	public static final String PATIENT_FILEPATH = "src/resources/Patient_List.csv";
	public static final String STAFF_FILEPATH = "src/resources/Staff_List.csv";
	public static final String DOCTOR_FILEPATH = "src/resources/Lookup_Doctor.csv";
	public static final String PHARMACIST_FILEPATH = "src/resources/Lookup_Pharmacist.csv";
	public static final String ADMIN_FILEPATH = "src/resources/Lookup_Admin.csv";
	public static final String APPOINTMENT_FILEPATH = "src/resources/Appointment_List.csv";
	public static final String OUTCOME_FILEPATH = "src/resources/Appointment_Outcome_List.csv";
	public static final String ORDER_FILEPATH = "src/resources/Order_List.csv";
	public static final String INVENTORY_FILEPATH = "src/resources/Medicine_List.csv";

	private static User currentUser;

	private UserService userService;
	private InventoryService inventoryService;
	// private AppointmentService appointmentService;
	private CsvReaderService csvReaderService;
	// private User currentUser; // IMPT this might not be able to downcast
	
	public AppController() {
		userService = new UserService();
		// inventoryService = new InventoryService();
		// appointmentService = new AppointmentService();
		csvReaderService = new CsvReaderService();
	}

	public void loadData() {

		try {
			userService.loadPatients(csvReaderService.loadData(PATIENT_FILEPATH));
			// userService.loadStaff(csvReaderService.loadData(STAFF_FILEPATH));
			// inventoryService.addMedicine(csvReaderService.read(INVENTORY_FILEPATH));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public User login(String id, String password) {
		return userService.findUser(id, password);
	}

	public void logout() {

	}

	public void testStart() {
		TestMenuService menuService = new TestMenuService();
		menuService.run();
	}

	// public void start() {
	// 	// display login
	// 	// login user
	// 	// loop (logout, login)
	// 	// TODO Should scanner be passed into menuService?
	// 	MenuService menuService = new MenuService();
	// 	BaseMenu loginMenu = new BaseMenu(, );
	// 	menuService.start();
	// 	while (currentUser == null) {
			
	// 		System.out.print("Enter your user ID: ");
    //         String id = scanner.nextLine();
            
    //         System.out.print("Enter your password: ");
    //         String password = scanner.nextLine();
    //         currentUser = login(id, password);

	// 		while (true) {
	// 			try {
	// 				menuService.getCurrentMenu().display();
	// 				menuService.handleUserInput(scanner.nextLine());
	// 			} catch (ExitApplication e) {
	// 				System.out.println(e.getMessage());
	// 				break;
	// 			} catch (Exception e) {}
	// 		}
	// 		scanner.close();
	// 	}
	// 	Scanner scanner = new Scanner(System.in);
	// 	MenuService menuService = new MenuService();
		
	// }

	private static void setCurrentUser(User user) {
		AppController.currentUser = user;
	}

	public static User getCurrentUser() {
		return AppController.currentUser;
	}
}