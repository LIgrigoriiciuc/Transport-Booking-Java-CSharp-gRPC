package Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class DateTimeUtils {
    public static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static String format(LocalDateTime dt) {
        return dt != null ? dt.format(FORMATTER) : "";
    }
    public static LocalDateTime parse(String dt) {
        try {
            return dt != null ? LocalDateTime.parse(dt, FORMATTER) : null;
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd HH:mm");
        }
    }
}