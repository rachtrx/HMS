package app.model.users.user_credentials;

import app.constants.exceptions.InvalidLengthException;
import app.constants.exceptions.InvalidPhoneNumberException;
import app.model.users.user_credentials.validators.IntegerValidator;
import app.model.users.user_credentials.validators.StringValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
* Phone number validator.
* Only supports Singapore (+65) numbers: https://krispcall.com/blog/singapore-phone-number-format/
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public final class PhoneNumber extends ValidatedData<Integer, String> implements IntegerValidator, StringValidator {
  // Constants START
  /**
   * All categories of phone numbers.
   */
  public static enum PhoneNumberType {
    HOME {
      @Override
      public String toString() {
        return "HOME";
      }
    },
    MOBILE {
      @Override
      public String toString() {
        return "MOBILE";
      }
    },
    VOIP {
      @Override
      public String toString() {
        return "VoIP";
      }
    },
  }
  
  /**
   * Maps first digit of phone number to its type.
   */
  private static HashMap<Integer, PhoneNumberType> identifierToPhoneNumberType = new HashMap<Integer, PhoneNumberType>() {{
    put(3, PhoneNumberType.VOIP);
    put(6, PhoneNumberType.HOME);
    put(8, PhoneNumberType.MOBILE);
    put(9, PhoneNumberType.MOBILE);
  }};
  // Constants END

  // Constructor START
  private PhoneNumberType type;
  
  /**
   * Constructor
   * 
   * @param number Phone number
   */
  public PhoneNumber(String number) throws Exception {
    super(number);
    setType();
  }

  /**
   * Constructor
   * 
   * @param number Phone number
   */
  public PhoneNumber(int number) throws Exception  {
    super(String.valueOf(number));
    setType();
  }
  // Constructor END

  // Validation START
  /**
   * Check for valid Singapore phone number.
   * 
   * @param number Phone number (String)
   * @return 8-digit phone number, if valid
   */

  @Override
  public Integer validate(String number) throws InvalidPhoneNumberException {
    this.validateString(number);
    return Integer.valueOf(number.substring(number.length() - 8));
  }

  // CALLED IN validateString
  @Override
  public void validateInteger(int number) throws InvalidPhoneNumberException {
    if (!identifierToPhoneNumberType.containsKey(number)) {
      throw new InvalidPhoneNumberException(String.format(
        "A Singapore (+65) phone number should begin with one of the following digits: %s",
        identifierToPhoneNumberType.keySet().stream()
          .map(String::valueOf)
          .collect(Collectors.joining(", "))
      ));
    }
  }

  @Override
  public void validateString(String number) throws InvalidPhoneNumberException {
    Pattern singaporePhoneNumberPattern = Pattern.compile(
      "^(?:\\+65)?(?<number>[0-9]{8})$",
      Pattern.CASE_INSENSITIVE
    );

    Matcher matcher = singaporePhoneNumberPattern.matcher(number);
    if (matcher.find()) {
        String numberStr = matcher.group("number");
        int firstDigit = Integer.parseInt(numberStr.substring(0, 1));
        this.validateInteger(firstDigit);
    } else {
      throw new InvalidPhoneNumberException("A Singapore (+65) phone number should contain 8 digits.");
    }
  }
  // Validation END

  // Getters & setters START
  /**
   * Get 8-digit phone number (int)
   * 
   * @return Phone number (int)
   */
  public int getNumber() {
    return this.getValue();
  }

  /**
   * Set 8-digit phone number (String)
   * 
   * @param number
   */
  public void setNumber(String number) throws Exception {
    this.setValue(number);
    setType();
  }

  public void setNumber(Integer number) throws Exception {
    this.setValue(String.valueOf(number));
    setType();
  }

  /**
   * Get phone number type
   * 
   * @return Phone number type
   */
  public PhoneNumberType getType() {
    return this.type;
  }

  /**
   * Set phone number type
   * 
   * @param type
   */
  private void setType() { // IMPT called only whenever phone number changes
    int firstDigit = Integer.parseInt(String.valueOf(this.getValue()).substring(0, 1));
    this.type = PhoneNumber.identifierToPhoneNumberType.get(firstDigit);
  }
  // Getters & setters END
}


