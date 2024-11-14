package app.constants.exceptions;
/**
* Error for incorrect length (e.g. username, password).
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class InvalidLengthException extends BaseCustomException {

  public InvalidLengthException(String message) {
      super(message);
  }
}