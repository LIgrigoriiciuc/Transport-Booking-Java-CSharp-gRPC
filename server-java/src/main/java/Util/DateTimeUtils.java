package Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class DateTimeUtils {
    public static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static String format(LocalDateTime dt) {
        return dt != null ? dt.format(FORMATTER) : "";
    }
    public static LocalDateTime parse(String dt) {
        return dt != null ? LocalDateTime.parse(dt, FORMATTER) : null;
    }
}