package app.model.users.user_credentials;

import app.constants.exceptions.InvalidCharacterException;
import app.constants.exceptions.InvalidLengthException;
import app.model.users.User;
import app.model.validators.IntegerValidator;
import app.model.validators.StringValidator;
import app.service.UserService;
import app.utils.LoggerUtils;
import app.utils.StringUtils;
import java.util.Optional;

/**
* Username validator.
*
* @author Luke Eng (@LEPK02)
* @author Rachmiel Teo (@rachtrx)
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
        super(StringUtils.parseUserInput(username));
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
        String parsedUsername = StringUtils.parseUserInput(username);
        Optional<User> existingUser = UserService.getAllUsers()
            .stream()
            .filter(user -> user.getUsername().equals(parsedUsername))
            .findFirst();
        if (existingUser.isPresent()) {
            throw new Exception("Username is already taken; please try another.");
        }
        this.setValue(parsedUsername);
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
            LoggerUtils.info("Username must be " + MIN_LENGTH + " to " + MAX_LENGTH + " characters long.");
            throw new InvalidLengthException("Username must be " + MIN_LENGTH + " to " + MAX_LENGTH + " characters long.");
        }
    }
}
