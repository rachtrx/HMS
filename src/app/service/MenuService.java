package app.service;

import app.model.user_input.menus.BaseMenu;
import app.model.user_input.menus.LandingMenu;

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

    public void next(String userInput) throws Exception {
        MenuService.currentMenu = MenuService.currentMenu.next(userInput);
        MenuService.currentMenu.display();
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
    }
}
