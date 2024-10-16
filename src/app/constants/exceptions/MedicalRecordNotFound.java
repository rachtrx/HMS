package app.constants.exceptions;
/**
* Error for missing medical records (for a patient).
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class MedicalRecordNotFound extends Exception {

  public MedicalRecordNotFound() {}

  public MedicalRecordNotFound(String message) {
     super(message);
  }
}