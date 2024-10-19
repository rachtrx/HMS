package app.constants.exceptions;
/**
* Base class for custom exception.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public abstract class BaseCustomException extends Exception {
    
    public BaseCustomException(String message) {
        super(message);
    }
}