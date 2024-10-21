package app.model.user_input.options;

import app.model.user_input.menus.BaseMenu;
import app.model.user_input.menus.LoginUsernameMenu;
import app.service.UserService;

/**
* Logout.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-20
*/
public class LogoutOption extends BaseOption {

    public LogoutOption() {
        super("^LO$|log( )?out(( )?\\(LO\\))?");
        this.setNumberedOption(false);
    }

    @Override
    public String getLabel() {
        return "Logout (LO)";
    }

    @Override
    public BaseMenu getNextMenu() {
        return new LoginUsernameMenu();
    }

    @Override
    public void executeAction() {
        UserService.logout();
    }
}