package app.model.validators;

public interface StringValidator {
    void validateString(String value) throws Exception;
}


// public abstract class PatternValidator implements Validator<String> {
//     protected Pattern validatorPattern;
//     protected Exception exception;

//     public PatternValidator(String pattern, Exception exception, String value) throws Exception {
//         this.validatorPattern = Pattern.compile(pattern);
//         this.exception = exception;
//         this.validate(value);
//     }

//     public final void validate(String value) throws Exception {
//         Matcher matcher = validatorPattern.matcher(value);
//         if (!matcher.find()) {
//             throw exception;
//         }
//     }
// }