package app.model.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringValidator<E extends Exception>{
    private Pattern validatorPattern;
    private E exception;
    private String value;
    
    public StringValidator(
        Pattern validatorPattern,
        E exception,
        String value
    ) throws E {
        this.validatorPattern = validatorPattern;
        this.exception = exception;
        this.value = this.validate(value);
    }

    public String get() {
        return value;
    }

    public final void set(String newValue) throws E {
        this.value = this.validate(newValue);
    }

    private String validate(String value) throws E {
        Matcher matcher = this.validatorPattern.matcher(value);
        if (matcher.find()) {
            return value;
        } else {
            throw this.exception;
        }
    }
}
