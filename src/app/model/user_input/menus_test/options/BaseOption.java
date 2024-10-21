package app.model.user_input.menus_test.options;

import app.model.user_input.menus_test.BaseMenu;
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

    public BaseOption() {};

    

    public void setMatchPattern(String matchPattern) {
        this.matchPattern = matchPattern;
    }

    public boolean isMatch(String userInput) {
        Matcher matcher = Pattern
            .compile(this.matchPattern, Pattern.CASE_INSENSITIVE)
            .matcher(userInput);
        return matcher.find();
    }
}
