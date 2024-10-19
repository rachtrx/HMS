package app.constants.exceptions;
/**
* Error for invalid Singapore phone numbers.
* https://krispcall.com/blog/singapore-phone-number-format/
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class InvalidPhoneNumberException extends BaseCustomException {

  public InvalidPhoneNumberException(String message) {
     super(message);
  }
}