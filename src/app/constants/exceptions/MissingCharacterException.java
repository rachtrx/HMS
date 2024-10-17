package app.constants.exceptions;
/**
* Error for missing characters (e.g. password).
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class MissingCharacterException extends Exception {

  public MissingCharacterException() {}

  public MissingCharacterException(String message) {
     super(message);
  }
}