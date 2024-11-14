package app.constants.exceptions;
/**
* Error for invalid characters (e.g. username).
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class InvalidCharacterException extends BaseCustomException {

  public InvalidCharacterException(String message) {
    super(message);
  }
}