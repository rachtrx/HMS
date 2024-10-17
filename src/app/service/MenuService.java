package app.service;

import app.constants.exceptions.ExitApplication;
import app.constants.exceptions.ItemNotFoundException;
import app.model.user_input.menus.BaseMenu;
import app.model.users.User;

import java.util.Scanner;

/**
* Controls which menus to show (Equivalent to machine in FSM)
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class MenuService {
    private BaseMenu currentMenu;

    public void handleUserInput(String userInput) {
        currentMenu.next(userInput);
    }

    public BaseMenu getCurrentMenu() {
        return currentMenu;
    }

    public BaseMenu getInitialMenu(User user) {
        
    }

    public void setCurrentMenu(BaseMenu currentMenu) {
        this.currentMenu = currentMenu;
    }

    public void exitApp() throws ExitApplication {
        throw new ExitApplication();
    }
}
