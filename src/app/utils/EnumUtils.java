package app.utils;

public class EnumUtils {
    public static <T extends Enum<T>> T fromString(Class<T> enumType, String value) {
        for (T constant : enumType.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(value)) {
                return constant;
            }
        }
        throw new IllegalArgumentException("No enum constant for the string: " + value);
    }
}
