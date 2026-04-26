package Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class DateTimeUtils {
    public static final DateTimeFormatter FORMATTER =
            new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd HH:mm")
                    .optionalStart()
                    .appendPattern(":ss")
                    .optionalEnd()
                    .toFormatter();
    public static String format(LocalDateTime dt) {
        return dt != null ? dt.format(FORMATTER) : "";
    }

    public static LocalDateTime parse(String dt) {
        if (dt == null || dt.isBlank()) return null;
        // normalize ISO format just in case
        return LocalDateTime.parse(dt.replace("T", " "), FORMATTER);
    }
}