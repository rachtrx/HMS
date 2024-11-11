package app.service;

import app.constants.exceptions.ExitApplication;
import app.model.user_input.MenuState;
import app.model.user_input.Menu;
import app.utils.LoggerUtils;
import java.util.HashMap;

/**
* Controls which menus to show (Equivalent to machine in FSM)
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class MenuService {
    private static Menu currentMenu = MenuState.LANDING.getMenu(new HashMap<>());

    public static void handleUserInput(String userInput) throws Exception {
        MenuState oldMenuState = MenuService.currentMenu.getMenuState();
        MenuState menuState = MenuService.currentMenu.handleUserInput(userInput);
        if (oldMenuState == menuState) return;
        setCurrentMenu(menuState);
    }

    public static Menu getCurrentMenu() {
        return MenuService.currentMenu;
    }

    public static void setCurrentMenu(MenuState newMenuState) throws ExitApplication{
        LoggerUtils.info("Next Menu State: " + newMenuState);
        if (newMenuState == null) {
            newMenuState = MenuState.getUserMainMenuState();
            if (newMenuState == null) throw new ExitApplication();
        }
        Menu newMenu = newMenuState.getMenu(MenuService.currentMenu.getFormData());
        MenuService.currentMenu.setNextMenu(newMenu);
        newMenu.setPreviousMenu(MenuService.currentMenu);
        MenuService.currentMenu = newMenu;
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        System.out.print("\n\n"); // add buffer rows between states 
    }
}
