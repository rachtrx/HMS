package app.service;

import java.util.HashMap;

import app.model.user_input.Menu;
import app.model.user_input.NewMenu;

/**
* Controls which menus to show (Equivalent to machine in FSM)
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class MenuService {
    private static NewMenu currentMenu = MenuState.LANDING.getMenu(new HashMap<String, Object>());

    public static void handleUserInput(String userInput) throws Exception {
        MenuService.currentMenu = MenuService.currentMenu.handleUserInput(userInput);
    }

    public static NewMenu getCurrentMenu() {
        return MenuService.currentMenu;
    }

    public static void setCurrentMenu(NewMenu currentMenu) {
        MenuService.currentMenu = currentMenu;
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        System.out.print("\n\n"); // add buffer rows between states 
    }
}
