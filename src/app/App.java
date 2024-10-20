package app;

import app.constants.exceptions.ExitApplication;
import app.controller.AppController;
import app.db.db;
import app.service.MenuService;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class App {
	private static Scanner scanner = new Scanner(System.in);

	private static void exitApplication() {
		MenuService.clearScreen();
		System.out.println(new ExitApplication().getMessage());
		App.scanner.close();
	}

    public static void main(String[] args) throws Exception {
		db.init();
		
		// MenuService.clearScreen(); // need to see logs
        MenuService.getCurrentMenu().display();
		
		while (true) {
			try {
				if (App.scanner.hasNextLine()) {
					MenuService.clearScreen();
					AppController.handleUserInput(App.scanner.nextLine());
				} else {
					App.exitApplication();
					break;
				}
			} catch (ExitApplication | NoSuchElementException | IllegalStateException e) {
				App.exitApplication();
				break;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Exception Caught");
				System.out.println(e.getMessage());
				MenuService.getCurrentMenu().display();
			}
		}
    }
}
