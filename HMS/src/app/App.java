package app;

import app.constants.exceptions.ExitApplication;
import app.controller.MenuService;
import app.db.DatabaseManager;
import app.utils.LoggerUtils;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class App {
	private static Scanner scanner = new Scanner(System.in);

	private static void exitApplication() {
		MenuService.clearScreen();
		System.out.println(new ExitApplication().getMessage());
		App.scanner.close();
		try {
			System.out.println("Saving changes to database...");
			DatabaseManager.stop();
			System.out.println("Changes successfully saved.");
		} catch (Exception e) {
			System.out.println("Error saving database: " + e.getMessage());
			// e.printStackTrace();
		}
		
	}

    public static void main(String[] args) throws Exception {
		DatabaseManager.start();
		
		MenuService.clearScreen();
        MenuService.getCurrentMenu().display();
		
		while (true) {
			try {
				LoggerUtils.info("Current Menu: " + MenuService.getCurrentMenu().getMenuState());
				if (App.scanner.hasNextLine()) {
					MenuService.clearScreen();
					MenuService.handleUserInput(App.scanner.nextLine());
					MenuService.getCurrentMenu().display();
				} else {
					App.exitApplication();
					break;
				}
			} catch (ExitApplication | NoSuchElementException | IllegalStateException e) {
				// e.printStackTrace();
				App.exitApplication();
				break;
			} catch (Exception e) {
				LoggerUtils.info("Exception Caught!");
				LoggerUtils.info(e.getMessage() + "\n");
				// e.printStackTrace();

				System.out.println(e.getMessage());
				MenuService.getCurrentMenu().display();
			}
		}
    }
}
