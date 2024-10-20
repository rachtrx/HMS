package app.model.user_input.options;

/**
* Menu option for user to select.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public abstract class BaseSelectOption implements BaseOption {
    private String matchPattern;

    public BaseSelectOption(String matchPattern) {
        this.matchPattern = matchPattern;
    };

    public String getMatchPattern() {
        return matchPattern;
    }

    public void setMatchPattern(String matchPattern) {
        this.matchPattern = matchPattern;
    }
}