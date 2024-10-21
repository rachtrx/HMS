package app;

import app.constants.exceptions.ExitApplication;
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
		
		MenuService.clearScreen();
        MenuService.getCurrentMenu().display();
		
		while (true) {
			try {
				if (App.scanner.hasNextLine()) {
					MenuService.clearScreen();
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
				System.out.println(e.getMessage() + "\n");
				MenuService.getCurrentMenu().display();
			}
		}
    }
}
