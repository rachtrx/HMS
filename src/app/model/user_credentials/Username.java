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
public final class Username extends ValidatedData<String, String> implements IntegerValidator, StringValidator {

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]+$";
    private static final int MIN_LENGTH = 4;
    private static final int MAX_LENGTH = 20;

    /**
    * Constructor
    * 
    * @param username Username
    */
    public Username(String username) throws Exception {
        super(username);
    }

    /** 
     * @return String
     */
    public String getUsername() {
        return this.getValue();
    }

    /** 
     * @param username
     * @throws InvalidLengthException
     * @throws InvalidCharacterException
     */
    public void setUsername(String username) throws Exception {
        this.setValue(username);
    }

    
    /** 
     * @param username
     * @return String
     * @throws InvalidCharacterException
     * @throws InvalidLengthException
     */
    @Override
    public String validate(String username) throws InvalidCharacterException, InvalidLengthException {
        validateString(username);
        validateInteger(username.length());
        return username;
    }

    
    /** 
     * @param username
     * @throws InvalidCharacterException
     */
    @Override
    public void validateString(String username) throws InvalidCharacterException {
        if (!username.matches(USERNAME_PATTERN)) {
            throw new InvalidCharacterException("Username contains invalid characters.");
        }
    }

    
    /** 
     * @param length
     * @throws InvalidLengthException
     */
    @Override
    public final void validateInteger(int length) throws InvalidLengthException {
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new InvalidLengthException("Username must be " + MIN_LENGTH + " to " + MAX_LENGTH + " characters long.");
        }
    }
}
