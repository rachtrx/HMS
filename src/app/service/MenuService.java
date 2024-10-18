package app.service;

import app.constants.exceptions.ExitApplication;
import app.model.user_input.menus.BaseMenu;
import app.model.user_input.options.BaseOption;
import java.util.List;
import java.util.stream.Collectors;

/**
* Controls which menus to show (Equivalent to machine in FSM)
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class MenuService {
    private BaseMenu currentMenu;

    public void handleUserInput(String userInput) throws Exception {
        this.currentMenu.next(userInput.trim().toLowerCase());
    }

    public BaseMenu getCurrentMenu() {
        return this.currentMenu;
    }

    public void setCurrentMenu(BaseMenu currentMenu) {
        this.currentMenu = currentMenu;
    }

    public void exitApp() throws ExitApplication {
        throw new ExitApplication();
    }

    public void next(String userInput) throws Exception {
        List<BaseOption> matchingOptions = this.currentMenu.getOptions()
            .stream()
            .filter(option -> option.isMatch(userInput))
            .collect(Collectors.toList());
        if (matchingOptions.size() < 1) {
            this.currentMenu.display(true);
        } else if (matchingOptions.size() > 1) {
            this.currentMenu.display(matchingOptions);
        } else {
            BaseOption option = matchingOptions.get(0);
            try {
                option.executeCallback();
                this.setCurrentMenu(option.getNextMenu());
            } catch (ExitApplication e) {
                throw e;
            } catch (Exception e) {
                return;
            } catch (Error e) {
                System.err.println("Something went wrong. Please contact your administrator and try again.");
                System.err.println("Exiting application...");
                throw new ExitApplication();
            }
            this.currentMenu.display();
        }
    }
}
