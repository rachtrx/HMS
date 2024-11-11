package app.utils;

import app.utils.DateTimeUtil.DateConditions;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
* LocalDate and LocalDateTime manipulation.
*
* @author Rachmiel Teo (@rachtrx)
* @version 1.0
* @since 2024-10-18
*/
public class DateTimeUtil {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FULL_DATE_FORMAT = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
    private static final DateTimeFormatter FULL_DATETIME_FORMAT = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy HH:mm:ss");

    private static final DateTimeFormatter SHORTEST_DATETIME_FORMAT = DateTimeFormatter.ofPattern("EEE, HH:mm");

    private static final DateTimeFormatter CSV_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String printShortDate(LocalDate date) {
        return date.format(DATE_FORMAT);
    }

    public static String printLongDate(LocalDate date) {
        return date.format(FULL_DATE_FORMAT);
    }

    public static String printShortDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMAT);
    }

    public static String printShortestDateTime(LocalDateTime dateTime) {
        return dateTime.format(SHORTEST_DATETIME_FORMAT);
    }

    public static String printLongDateTime(LocalDateTime dateTime) {
        return dateTime.format(FULL_DATETIME_FORMAT);
    }

    public static String formatCsvDate(String dateStr) {
        try {
            LocalDate parsedDate = LocalDate.parse(dateStr, CSV_DATE_FORMAT);
            return printShortDate(parsedDate);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format, expected yyyy-MM-dd: " + e.getMessage());
            return null;
        }
    }

    public static LocalDate parseShortDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format, expected dd/MM/yyyy: " + e.getMessage());
            return null;
        }
    }

    public static LocalDateTime parseShortDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, DATETIME_FORMAT);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date and time format, expected dd/MM/yyyy HH:mm:ss: " + e.getMessage());
            return null;
        }
    }

    public static LocalDateTime parseLongDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, FULL_DATETIME_FORMAT);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date and time format, expected EEEE, MMMM d, yyyy HH:mm:ss: " + e.getMessage());
            return null;
        }
    }

    public static LocalDateTime addWorkingDays(LocalDateTime dateTime, int daysToAdd) {
        LocalDateTime result = dateTime;
        int addedDays = 0;
        while (addedDays < daysToAdd) {
            result = result.plusDays(1);
            if (!(
                result.getDayOfWeek() == DayOfWeek.SATURDAY ||
                result.getDayOfWeek() == DayOfWeek.SUNDAY
            )) {
                addedDays++;
            }
        }
        return result;
    }

    // Validate user date input START
    public static enum DateConditions {
        PAST,
        PAST_OR_PRESENT,
        FUTURE,
        FUTURE_OR_PRESENT
    }

    public static void validateUserDateInput(
        LocalDateTime originalDate, LocalDateTime userDate, String dateCondition
    ) throws Exception {

        if (dateCondition == null) {
            throw new IllegalArgumentException("Missing date validation condition");
        }
        
        DateConditions criteria = DateConditions.valueOf(dateCondition);

        if (
            criteria == DateConditions.PAST_OR_PRESENT &&
            userDate.isAfter(originalDate)
        ) {
            throw new IllegalArgumentException(String.format(
                "Date must be earlier than or equal to %s",
                DateTimeUtil.printLongDateTime(originalDate)
            ));
        } else if (
            criteria == DateConditions.PAST &&
            (userDate.isAfter(originalDate) || userDate.isEqual(originalDate))
        ) {
            throw new IllegalArgumentException(String.format(
                "Date must be earlier than %s",
                DateTimeUtil.printLongDateTime(originalDate)
            ));
        } else if (
            criteria == DateConditions.FUTURE_OR_PRESENT &&
            userDate.isBefore(originalDate)
        ) {
            throw new IllegalArgumentException(String.format(
                "Date must be later than or equal to %s",
                DateTimeUtil.printLongDateTime(originalDate)
            ));
        } else if (
            criteria == DateConditions.FUTURE &&
            (userDate.isBefore(originalDate) || userDate.isEqual(originalDate))
        ) {
            throw new IllegalArgumentException(String.format(
                "Date must be later than %s",
                DateTimeUtil.printLongDateTime(originalDate)
            ));
        }
    }

    // public static void validateYear(
    //     String userInput,
    //     String dateCondition
    // ) throws Exception {
    //     DateTimeUtil.validateUserDateInput(userInput, dateCondition, LocalDate.now().getYear());
    //     if (
    //         Integer.parseInt(userInput) > LocalDate.now().getYear() + 1 ||
    //         Integer.parseInt(userInput) < LocalDate.now().getYear() - 1
    //     ) {
    //         throw new Exception(String.format(
    //             "Please enter a number between %d to %d",
    //             LocalDate.now().getYear() - 1,
    //             LocalDate.now().getYear() + 1
    //         ));
    //     }
    // }

    // public static void validateMonth(
    //     String userInput,
    //     String dateCondition
    // ) throws Exception {
    //     DateTimeUtil.validateUserInput(userInput, dateCondition, LocalDate.now().getMonthValue());
    //     if (Integer.parseInt(userInput) < 1 || Integer.parseInt(userInput) > 12) {
    //         throw new Exception("Please enter a number between 1 (Jan) to 12 (Dec)");
    //     }
    // }

    // public static void validateDay(
    //     String userInput,
    //     String dateCondition
    // ) throws Exception {
    //     DateTimeUtil.validateUserInput(userInput, dateCondition, LocalDate.now().getDayOfMonth());
    // }

    // public static void validateHour(
    //     String userInput,
    //     String dateCondition
    // ) throws Exception {
    //     DateTimeUtil.validateUserInput(userInput, dateCondition, LocalTime.now().getHour());
    //     if (
    //         Integer.parseInt(userInput) <= Timeslot.lastSlotStartTime.getHour()  ||
    //         Integer.parseInt(userInput) >= Timeslot.firstSlotStartTime.getHour()
    //     ) {
    //         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
    //         throw new Exception(String.format(
    //             "Please enter a time during office hours (%sH - %sH)",
    //             Timeslot.lastSlotStartTime.format(formatter),
    //             Timeslot.firstSlotStartTime.format(formatter)
    //         ));
    //     }
    // }
    // Validate user date input START
}