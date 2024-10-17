package app.constants.exceptions;
/**
* Error for invalid emails.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class InvalidEmailException extends Exception {

  public InvalidEmailException() {}

  public InvalidEmailException(String message) {
     super(message);
  }
}