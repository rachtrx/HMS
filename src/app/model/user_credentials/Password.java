package app.model.user_credentials;

import app.constants.exceptions.InvalidCharacterException;
import app.constants.exceptions.InvalidLengthException;
import app.constants.exceptions.MissingCharacterException;
import app.model.validators.IntegerValidator;
import app.model.validators.StringValidator;

/**
* Password validator.
*
* @author Luke Eng (@LEPK02)
* @author Rachmiel Teo (@rachtrx)
* @version 1.0
* @since 2024-10-17
*/
public final class Password extends ValidatedData<String, String> implements StringValidator, IntegerValidator {
    private final int MIN_LENGTH = 8;
    private final int MAX_LENGTH = 32;
    private final String LOWERCASE_PATTERN = ".*[a-z].*";
    private final String UPPERCASE_PATTERN = ".*[A-Z].*";
    private final String CHARACTER_PATTERN = ".*[!@#$%^&*].*";
    // private final String WHITESPACE_PATTERN = ".*\\w.*";
    
    /**
    * Constructor
    * 
    * @param password Password
    */
    public Password(String password) throws Exception {
        super(password);
    }
    
    /** 
     * @return String
     */
    public String getPassword() {
        return this.getValue();
    }

    public void setPassword(String password) throws Exception {
        this.setValue(password);
    }

    /** 
     * @param password
     * @throws InvalidLengthException
     * @throws MissingCharacterException
     */

    @Override
    public String validate(String password) throws InvalidCharacterException, MissingCharacterException, InvalidLengthException {
        validateString(password);
        validateInteger(password.length());
        return password;
    }

    @Override
    public void validateString(String password) throws InvalidCharacterException, MissingCharacterException {
        if (!password.matches(LOWERCASE_PATTERN)) throw new InvalidCharacterException("At least one lowercase character is required."); 
        if (!password.matches(UPPERCASE_PATTERN)) throw new InvalidCharacterException("At least one uppercase character is required.");
        if (!password.matches(CHARACTER_PATTERN)) throw new MissingCharacterException("At least one special character is required; i.e. !@#$%^&* ");
        // if (password.matches(WHITESPACE_PATTERN)) throw new InvalidCharacterException("Password cannot contain whitespace.");
    }

    @Override
    public void validateInteger(int length) throws InvalidLengthException {
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new InvalidLengthException("Password must be " + MIN_LENGTH + " to " + MAX_LENGTH + " characters long.");
        }
    }
}
