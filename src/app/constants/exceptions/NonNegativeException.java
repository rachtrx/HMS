package app.constants.exceptions;
/**
* Throw this when number should not be less than zero.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class NonNegativeException extends BaseCustomException {

  public NonNegativeException(String message) {
      super(message);
  }
}