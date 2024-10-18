package app.model.user_input.options;

import app.model.user_input.menus.BaseMenu;
import app.types.ThrowableFunction;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* Menu option. (Equivalent to transition + actions in FSM)
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public abstract class BaseOption {
    protected String label;
    protected String matchPattern;
    protected BaseMenu nextMenu;
    protected ThrowableFunction<List<Object>, Object, Exception> callback;
    protected List<Object> callbackArguments;

    public BaseOption() {};

    public String getLabel() {
        return label;
    }

    public BaseMenu getNextMenu() {
        return nextMenu;
    }

    public String getMatchPattern() {
        return matchPattern;
    }

    public void setMatchPattern(String matchPattern) {
        this.matchPattern = matchPattern;
    }

    public boolean isMatch(String userInput) {
        Matcher matcher = Pattern
            .compile(this.matchPattern, Pattern.CASE_INSENSITIVE)
            .matcher(userInput);
        return matcher.find();
    }

    public Object executeCallback() throws Exception {
        return this.callback.apply(this.callbackArguments);
    }
}