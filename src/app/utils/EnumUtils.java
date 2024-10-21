package app.utils;

/**
* Enum manipulation.
*
* @author Rachmiel Teo (@rachtrx)
* @version 1.0
* @since 2024-10-18
*/
public class EnumUtils {
    public static <T extends Enum<T>> T fromString(Class<T> enumType, String value) {
        for (T constant : enumType.getEnumConstants()) {
            if (constant.toString().equalsIgnoreCase(value)) {
                return constant;
            }
        }
        throw new IllegalArgumentException("No enum constant for the string: " + value);
    }
}
