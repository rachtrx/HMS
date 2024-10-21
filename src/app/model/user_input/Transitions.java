package app.model.user_input;

import app.utils.ThrowableFunction;
import java.util.ArrayList;
import java.util.List;

/**
* Source menu, destination menu, trigger (regex match of user input), action (business logic)
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-21
*/
public enum Transitions {
    LANDING_TO_LOGIN_USERNAME(
        States.LANDING, States.LOGIN_USERNAME, params -> null,
        new ArrayList<>(),".*", true
    );

    private final States source;
    private final States destination;
    private final ThrowableFunction<List<Object>, List<Object>, Exception> action;
    private final List<Object> actionArguments;
    private final String matchPattern;
    private final boolean parseUserInput;

    Transitions(
        States source,
        States destination,
        ThrowableFunction<List<Object>, List<Object>, Exception> action,
        List<Object> actionArguments,
        String matchPattern,
        boolean parseUserInput
    ) {
        this.source = source;
        this.destination = destination;
        this.action = action;
        this.actionArguments = actionArguments;
        this.matchPattern = matchPattern;
        this.parseUserInput = parseUserInput;
    }

    public void executeAction() throws Exception {
        this.action.apply(this.actionArguments);
    }

    public String getMatchPattern() {
        return matchPattern;
    }

    public States getSource() {
        return source;
    }

    public States getDestination() {
        return destination;
    }

    public boolean shouldParseUserInput() {
        return parseUserInput;
    }
}
