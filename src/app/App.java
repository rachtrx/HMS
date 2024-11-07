package app;

import app.constants.exceptions.ExitApplication;
import app.db.DatabaseManager;
import app.service.MenuService;
import app.utils.LoggerUtils;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class App {
	private static Scanner scanner = new Scanner(System.in);

	private static void exitApplication() {
		// MenuService.clearScreen();
		System.out.println(new ExitApplication().getMessage());
		App.scanner.close();
		try {
			DatabaseManager.stop();
		} catch (Exception e) {
			System.out.println("Error saving database: " + e.getMessage());
			e.printStackTrace();
		}
		
	}

    public static void main(String[] args) throws Exception {
		DatabaseManager.start();
		
		// MenuService.clearScreen();
        MenuService.getCurrentMenu().display();
		
		while (true) {
			try {
				System.out.printf("Current Menu: %s%n", MenuService.getCurrentMenu());
				if (App.scanner.hasNextLine()) {
					// MenuService.clearScreen(); // TODO uncomment once done
					MenuService.handleUserInput(App.scanner.nextLine());
					MenuService.getCurrentMenu().display();
				} else {
					App.exitApplication();
					break;
				}
			} catch (ExitApplication | NoSuchElementException | IllegalStateException e) {
				App.exitApplication();
				break;
			} catch (Exception e) {
				LoggerUtils.info(e.getMessage() + "\n");
				LoggerUtils.info("Exception Caught!");

				if (MenuService.getCurrentMenu() != MenuService.getCurrentMenu().getExitMenu()) {
					LoggerUtils.info("Same singleton instance"); // TODO REMOVE
					MenuService.getCurrentMenu().setDataFromPreviousMenu(null);
				}
				MenuService.setCurrentMenu(MenuService.getCurrentMenu().getExitMenu()); // no setting of args ie. clean state
				MenuService.getCurrentMenu().display();
			}
		}
    }
}
