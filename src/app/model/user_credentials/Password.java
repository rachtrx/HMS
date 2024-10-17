package app.model.user_credentials;

import app.constants.exceptions.InvalidLengthException;
import app.constants.exceptions.MissingCharacterException;
import app.model.validators.Validator;
import java.util.regex.Pattern;

/**
* Password validator.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class Password {
    private final Validator<MissingCharacterException> lowerCharacterValidator;
    private final Validator<MissingCharacterException> upperCharacterValidator;
    private final Validator<MissingCharacterException> specialCharacterValidator;
    private final int MIN_LENGTH = 8;
    private final int MAX_LENGTH = 32;
    
    /**
    * Constructor
    * 
    * @param password Password
    */
    public Password(String password) throws InvalidLengthException, MissingCharacterException {
        this.validatePasswordLength(password);
        this.lowerCharacterValidator = new Validator<>(
            Pattern.compile("[a-z]+"),
            new MissingCharacterException("At least one lowercase character is required."),
            password
        );
        this.upperCharacterValidator = new Validator<>(
            Pattern.compile("[A-Z]+"),
            new MissingCharacterException("At least one lowercase character is required."),
            password
        );
        this.specialCharacterValidator = new Validator<>(
            Pattern.compile("[!@#$%^&*]+"),
            new MissingCharacterException("At least one special character is required; i.e. !@#$%^&* "),
            password
        );
    }  
    
    /** 
     * @return String
     */
    public String getPassword() {
        return this.specialCharacterValidator.get();
    }
    
    
    /** 
     * @param password
     * @throws InvalidLengthException
     * @throws MissingCharacterException
     */
    public final void setPassword(String password) throws InvalidLengthException, MissingCharacterException {
        this.validatePasswordLength(password);
        this.lowerCharacterValidator.set(password);
        this.upperCharacterValidator.set(password);
        this.specialCharacterValidator.set(password);
    }

    
    /** 
     * @param password
     * @throws InvalidLengthException
     */
    private void validatePasswordLength(String password) throws InvalidLengthException {
        if (password.length() < this.MIN_LENGTH || password.length() > this.MAX_LENGTH) {
            throw new InvalidLengthException(String.format(
                "Password must be %d to %d characters long.",
                this.MIN_LENGTH,
                this.MAX_LENGTH
            ));
        }
    }
}
