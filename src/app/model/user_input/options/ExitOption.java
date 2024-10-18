package app.model.user_input.options;

import app.constants.exceptions.ExitApplication;
import java.util.ArrayList;

/**
* Exit application.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-18
*/
public class ExitOption extends BaseOption {
    public ExitOption() {
        this.label = "Exit Application";
        this.matchPattern = "exit( )?((app)?lication)";
        this.nextMenu = null;
        this.callback = (_a) -> { throw new ExitApplication(); };
        this.callbackArguments = new ArrayList<>();
    }   
}