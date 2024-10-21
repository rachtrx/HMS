package app.service;

import app.model.user_input.menus.BaseMenu;
import app.model.user_input.menus.LandingMenu;
import app.model.user_input.menus.LoggedInMenu;
import app.model.user_input.menus.PatientMainMenu;
import app.model.users.Patient;
import app.model.users.User;

/**
* Controls which menus to show (Equivalent to machine in FSM)
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class MenuService {
    
    private static BaseMenu currentMenu = new LandingMenu();
    
    public static BaseMenu getCurrentMenu() {
        return currentMenu;
    }

    public static void setCurrentMenu(BaseMenu currentMenu) {
        MenuService.currentMenu = currentMenu;
    }

    public void handleUserInput(String userInput) throws Exception {
        MenuService.currentMenu = MenuService.currentMenu.handleUserInput(userInput);
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
    }

    // Get logged in main menus - START
    // TODO: new menu for each user (e.g. patient, doctor)
    public static LoggedInMenu getLoggedInUserMenu(User user) throws Exception {
        // TODO: test - throw error instead of returning new menu
        // throw new Exception("Undefined user type");
        return new PatientMainMenu();
    }

    public static LoggedInMenu getLoggedInUserMenu(Patient patient) {
        return new PatientMainMenu();
    }
    // Get logged in main menus - END
}
