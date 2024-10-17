package app.model.user_input.options;

import app.model.user_input.menus.BaseMenu;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
* Menu option. (Equivalent to transition + actions in FSM)
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public abstract class BaseOption {
    private String displayText;
    private Pattern matchPattern;
    private BaseMenu nextMenu;
    private Function callback;

    public BaseOption(
        String displayText,
        Pattern matchPattern,
        BaseMenu nextMenu,
        Function callback
    ) {
        this.displayText = displayText;
        this.matchPattern = matchPattern;
        this.nextMenu = nextMenu;
        this.callback = callback;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public Pattern getMatchPattern() {
        return matchPattern;
    }

    public void setMatchPattern(Pattern matchPattern) {
        this.matchPattern = matchPattern;
    }

    public BaseMenu getNextMenu() {
        return nextMenu;
    }

    public void setNextMenu(BaseMenu nextMenu) {
        this.nextMenu = nextMenu;
    }

    public Function getCallback() {
        return callback;
    }

    public void setCallback(Function callback) {
        this.callback = callback;
    }
}