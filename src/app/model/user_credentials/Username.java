package app.model.user_credentials;

import app.constants.exceptions.InvalidCharacterException;
import app.constants.exceptions.InvalidLengthException;
import app.model.validators.IntegerValidator;
import app.model.validators.StringValidator;

/**
* Username validator.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class Username implements IntegerValidator, StringValidator {

    private String username;

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]+$";
    private static final int MIN_LENGTH = 4;
    private static final int MAX_LENGTH = 20;

    /**
    * Constructor
    * 
    * @param username Username
    */
    public Username(String value) throws InvalidLengthException, InvalidCharacterException {
        this.setUsername(value);
    }

    /** 
     * @return String
     */
    public String getUsername() {
        return this.username;
    }

    /** 
     * @param username
     * @throws InvalidLengthException
     * @throws InvalidCharacterException
     */
    public final void setUsername(String value) throws InvalidLengthException, InvalidCharacterException {
        this.validate(value);
        this.username = value;
    }

    @Override
    public void validateString(String value) throws InvalidCharacterException {
        if (!value.matches(USERNAME_PATTERN)) {
            throw new InvalidCharacterException("Username contains invalid characters.");
        }
    }

    @Override
    public void validateInteger(int length) throws InvalidLengthException {
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new InvalidLengthException("Username must be " + MIN_LENGTH + " to " + MAX_LENGTH + " characters long.");
        }
    }

    public void validate(String value) throws InvalidCharacterException, InvalidLengthException {
        validateString(value);
        validateInteger(value.length());
    }
    
    
}
