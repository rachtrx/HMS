package app.constants.exceptions;
/**
* Error for missing appointment records (for a patient).
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class AppointmentNotFound extends Exception {

  public AppointmentNotFound() {}

  public AppointmentNotFound(String message) {
     super(message);
  }
}