package app.model.user_input.options;

import app.model.user_input.menus.BaseMenu;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
* Exit application.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class ExitOption extends BaseOption {
    public ExitOption(String displayText, BaseMenu nextMenu, Function<String, Void> callback) {
        super(
            displayText,
            Pattern.compile("Exit|Exit Application", Pattern.CASE_INSENSITIVE),
            nextMenu,
            callback
        );
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