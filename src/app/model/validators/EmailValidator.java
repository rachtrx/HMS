public class EmailValidator extends PatternValidator {

    public EmailValidator(String value) throws Exception {
        super("^\\S+@\\S+\\.\\S+$", new InvalidEmailException("Email format is invalid."), value);
    }
}