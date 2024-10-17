package app.model.validators;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import app.constants.exceptions.InvalidCharacterException;

public abstract class PatternValidator<String> implements Validator<String> {
    protected Pattern validatorPattern;
    protected Exception exception;

    public PatternValidator(String pattern, Exception exception, String value) throws Exception {
        this.validatorPattern = Pattern.compile(pattern);
        this.exception = exception;
        this.validate(value);
    }

    public final void validate(String value) throws Exception {
        Matcher matcher = validatorPattern.matcher(value);
        if (!matcher.find()) {
            throw exception;
        }
    }
}