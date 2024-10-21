package app.model.user_credentials;

import app.constants.exceptions.InvalidCharacterException;
import app.constants.exceptions.InvalidEmailException;
import app.constants.exceptions.InvalidLengthException;
import app.constants.exceptions.MissingCharacterException;
import app.model.validators.StringValidator;

/**
* Email validator.
*
* @author Luke Eng (@LEPK02)
* @author Rachmiel Teo (@rachtrx)
* @version 1.0
* @since 2024-10-17
*/
public class Email extends ValidatedData<String, String> implements StringValidator {

    private final String EMAIL_PATTERN = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    
    /**
    * Constructor
    * 
    * @param email Email
    */
    public Email(String email) throws Exception {
        super(email);
    }

    @Override
    public void validateString(String email) throws InvalidCharacterException, MissingCharacterException {
        if (!email.matches(EMAIL_PATTERN)) throw new InvalidCharacterException("Please enter a valid email address."); 
    }

    @Override
    public String validate(String email) throws InvalidCharacterException, MissingCharacterException, InvalidLengthException {
        validateString(email);
        return email;
    }
    
    
    /** 
     * @return String
     */
    public String getEmail() {
        return this.getValue();
    }
    
    
    /** 
     * @param email
     * @throws InvalidEmailException
     */
    public final void setEmail(String email) throws Exception {
        this.setValue(email);
    }
}


