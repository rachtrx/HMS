package app.model.user_input;

import java.util.regex.Pattern;

/**
* Menu option.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public abstract class Option {
    private String displayText;
    private Pattern matchPattern;
}