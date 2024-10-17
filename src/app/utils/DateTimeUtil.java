package app.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeUtil {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FULL_DATE_FORMAT = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
    private static final DateTimeFormatter FULL_DATETIME_FORMAT = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy HH:mm:ss");

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
}