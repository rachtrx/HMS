public class UsernameValidator extends PatternValidator {

    public UsernameValidator(String value) throws Exception {
        super("^[a-zA-Z0-9_]+$", new InvalidCharacterException("Username contains invalid characters."), value);
    }
}