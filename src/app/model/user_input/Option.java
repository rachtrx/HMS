package app.model.user_input;

import app.model.user_input.FunctionalInterfaces.NextAction;
import java.util.LinkedHashMap;
import java.util.Map;

public class Option extends Input {
    protected  final String matchPattern;
    protected final OptionType optionType ;

    private Map<String, String> displayFields;

    public enum OptionType {
        NUMBERED, UNNUMBERED, DISPLAY
    }

    public Option(
        String matchPattern,
        OptionType optionType,
        Map<String, String> displayFields
    ) {
        this.matchPattern = matchPattern;
        this.optionType = optionType;
        this.displayFields = new LinkedHashMap<>(displayFields);
    };

    public Map<String, String> getDisplayFields() {
        return displayFields;
    }

    public OptionType getOptionType() {
        return optionType;
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