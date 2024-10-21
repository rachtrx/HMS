package app.model.user_input.options;

import app.model.user_input.menus.BaseMenu;

/**
* Menu option. (Equivalent to transition + actions in FSM)
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public abstract class BaseOption {
    private final String matchPattern;
    private boolean numberedOption = true;

    public BaseOption(String matchPattern) {
        this.matchPattern = matchPattern;
    };

    public abstract String getLabel();

    public abstract BaseMenu getNextMenu();

    public abstract void executeAction() throws Exception;

    public String getMatchPattern() {
        return this.matchPattern;
    }

    public boolean isNumberedOption() {
        return this.numberedOption;
    }

    public void setNumberedOption(boolean numberedOption) {
        this.numberedOption = numberedOption;
    }
}