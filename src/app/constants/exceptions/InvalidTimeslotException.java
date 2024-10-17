package app.constants.exceptions;
/**
* Error for invalid timeslot.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class InvalidTimeslotException extends Exception {

  public InvalidTimeslotException() {}

  public InvalidTimeslotException(String message) {
     super(message);
  }
}