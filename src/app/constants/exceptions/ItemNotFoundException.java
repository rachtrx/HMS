package app.constants.exceptions;
/**
* Throw this when item in a list not found.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class ItemNotFoundException extends BaseCustomException {

  public ItemNotFoundException(String message) {
     super(message);
  }
}