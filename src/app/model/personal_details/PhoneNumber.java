package app.model.personal_details;

import app.constants.exceptions.InvalidPhoneNumberException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;

/**
* Phone number validator.
* Only supports Singapore (+65) numbers: https://krispcall.com/blog/singapore-phone-number-format/
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class PhoneNumber {
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
  private int number;
  private PhoneNumberType type;
  
  /**
   * Constructor
   * 
   * @param number Phone number
   */
  public PhoneNumber(String number) throws InvalidPhoneNumberException {
    this(number, PhoneNumber.getPhoneNumberTypeFromNumber(number));
  }

  /**
   * Constructor
   * 
   * @param number Phone number
   */
  public PhoneNumber(int number) throws InvalidPhoneNumberException  {
    this(Integer.toString(number));
  }

  /**
   * Constructor
   * 
   * @param number Phone number
   * @param type Phone number type
   */
  public PhoneNumber(String number, PhoneNumberType type) throws InvalidPhoneNumberException {
    this.number = Integer.parseInt(PhoneNumber.validateNumber(number));
    this.type = type;
  }

  /**
   * Constructor
   * 
   * @param number Phone number
   * @param type Phone number type
   */
  public PhoneNumber(int number, PhoneNumberType type) throws InvalidPhoneNumberException  {
    this(Integer.toString(number), type);
  }
  // Constructor END

  // Validation START
  /**
   * Check for valid Singapore phone number.
   * 
   * @param number Phone number (String)
   * @return 8-digit phone number, if valid
   */
  private static String validateNumber(String number) throws InvalidPhoneNumberException {
    Pattern singaporePhoneNumberPattern = Pattern.compile(
    "^(?:\\+65)?(?<number>[0-9]{8})$",
    Pattern.CASE_INSENSITIVE
    );
    Matcher matcher = singaporePhoneNumberPattern.matcher(number);
    if (matcher.find()) {
      if (matcher.group("number") == null) {
        throw new InvalidPhoneNumberException(String.format(
          "A Singapore (+65) phone number should begin with one of the following digits: %s",
          identifierToPhoneNumberType
            .keySet()
            .stream()
            .map(i -> i.toString())
            .reduce((i, j) -> String.format("%s, %s", i, j))
        ));
      } else {
        return matcher.group("number");
      }
    } else {
      throw new InvalidPhoneNumberException("A Singapore (+65) phone number should contain 8 digits.");
    }
  }

  /**
   * Gets phone number type based on phone number.
   * 
   * @param number Phone number (String)
   * @return Phone number type
   */
  private static PhoneNumberType getPhoneNumberTypeFromNumber(String number) {
    return PhoneNumber.identifierToPhoneNumberType.get(
      Integer.valueOf(number.substring(0, 1))
    );
  }
  // Validation END

  // Getters & setters START
  /**
   * Get 8-digit phone number (int)
   * 
   * @return Phone number (int)
   */
  public int getNumber() {
    return this.number;
  }

  /**
   * Set 8-digit phone number (String)
   * 
   * @param number
   */
  public void setNumber(String number) throws InvalidPhoneNumberException {
    this.number = Integer.parseInt(PhoneNumber.validateNumber(number));
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
  public void setType(PhoneNumberType type) {
    this.type = type;
  }
  // Getters & setters END
}


