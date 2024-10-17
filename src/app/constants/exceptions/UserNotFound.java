package app.constants.exceptions;
/**
* Error for missing user.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class UserNotFound extends Exception {

  public UserNotFound() {}

  public UserNotFound(String message) {
     super(message);
  }
}