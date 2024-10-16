package app.model.personal_details;

import app.constants.exceptions.InvalidEmailException;
import app.model.validators.StringValidator;
import java.util.regex.Pattern;

/**
* Email validator.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class Email {
    private final StringValidator<InvalidEmailException> validator;
    
    /**
    * Constructor
    * 
    * @param email Email
    */
    public Email(String email) throws InvalidEmailException {
        this.validator = new StringValidator<>(
            Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"),
            new InvalidEmailException("Please enter a valid email address."),
            email
        );
    }
    
    
    /** 
     * @return String
     */
    public String getEmail() {
        return this.validator.get();
    }
    
    
    /** 
     * @param email
     * @throws InvalidEmailException
     */
    public final void setEmail(String email) throws InvalidEmailException {
        this.validator.set(email);
    }
}


