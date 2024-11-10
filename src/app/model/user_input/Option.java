package app.model.user_input;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import app.model.user_input.FunctionalInterfaces.NextAction;

public class Option extends Input {
    protected  final String matchPattern;
    protected final boolean isNumberedOption;

    private Map<String, String> displayFields;

    public Option(
        String matchPattern,
        boolean isNumberedOption,
        Map<String, String> displayFields
    ) {
        this.matchPattern = matchPattern;
        this.isNumberedOption = isNumberedOption;

        this.displayFields = new LinkedHashMap<>(displayFields);
    };

    public Map<String, String> getDisplayFields() {
        return displayFields;
    }

    public boolean isNumberedOption() {
        return isNumberedOption;
    }

    @Override
    public Option setNextMenuState(MenuState nextState) {
        super.setNextMenuState(nextState);
        return this;
    }

    @Override
    public Option setExitMenuState(MenuState exitState) {
        super.setExitMenuState(exitState);
        return this;
    }

    @Override
    public Option setNextAction(NextAction nextAction) {
        super.setNextAction(nextAction);
        return this;
    }

    @Override
    public Option setEditRedirect(boolean isEditRedirect) {
        super.setEditRedirect(isEditRedirect);
        return this;
    }

    @Override
    public Option setRequiresConfirmation(boolean requiresConfirmation) {
        super.setRequiresConfirmation(requiresConfirmation);
        return this;
    }
}