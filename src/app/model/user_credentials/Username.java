package app.model.user_credentials;

import app.constants.exceptions.InvalidCharacterException;
import app.constants.exceptions.InvalidLengthException;
import app.model.validators.Validator;
import java.util.regex.Pattern;

/**
* Username validator.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class Username {
    private final Validator<InvalidCharacterException> invalidCharacterValidator;
    private final int MIN_LENGTH = 4;
    private final int MAX_LENGTH = 20;
    
    /**
    * Constructor
    * 
    * @param username Username
    */
    public Username(String username) throws InvalidLengthException, InvalidCharacterException {
        this.validateUsernameLength(username);
        this.invalidCharacterValidator = new Validator<>(
            Pattern.compile("[0-9A-Za-z_]+"),
            new InvalidCharacterException("Only letters, numbers and underscores (_) are allowed."),
            username
        );
    }
    
    /** 
     * @return String
     */
    public String getUsername() {
        return this.invalidCharacterValidator.get();
    }
    
    /** 
     * @param username
     * @throws InvalidLengthException
     * @throws InvalidCharacterException
     */
    public final void setUsername(String username) throws InvalidLengthException, InvalidCharacterException {
        this.validateUsernameLength(username);
        this.invalidCharacterValidator.set(username);
    }
    
    /** 
     * @param username
     * @throws InvalidLengthException
     */
    private void validateUsernameLength(String username) throws InvalidLengthException {
        if (username.length() < this.MIN_LENGTH || username.length() > this.MAX_LENGTH) {
            throw new InvalidLengthException(String.format(
                "Username must be %d to %d characters long.",
                this.MIN_LENGTH,
                this.MAX_LENGTH
            ));
        }
    }
}
