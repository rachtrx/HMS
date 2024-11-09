package app.model.user_input;

import java.util.LinkedHashMap;
import java.util.Map;

public class Option extends Field {
    private final String matchPattern;
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
}