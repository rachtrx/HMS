package app.model.user_input.options;

import app.constants.exceptions.ExitApplication;
import app.model.user_input.menus.BaseMenu;

/**
* Exit application.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-18
*/
public class ExitOption extends BaseSelectOption {
    public ExitOption() {
        super("exit(( )?((app)?plication)?)?");
    }

    @Override
    public String getLabel() {
        return "Exit Application";
    }

    @Override
    public BaseMenu getNextMenu() {
        return null;
    }

    @Override
    public void executeAction() throws ExitApplication {
        throw new ExitApplication();
    }
}