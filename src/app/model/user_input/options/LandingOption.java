package app.model.user_input.options;

import app.model.user_input.menus.BaseMenu;
import app.model.user_input.menus.LoginUsernameMenu;

/**
* Continue to application.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-18
*/
public class LandingOption extends BaseSelectOption {
    public LandingOption() {
        super(".*");
    }

    @Override
    public String getLabel() {
        return "Start Application";
    }

    @Override
    public BaseMenu getNextMenu() {
        return new LoginUsernameMenu();
    }

    @Override
    public void executeAction() throws Exception {
        // TODO Initialise application here (e.g. load from init CSVs)
        
    }
}