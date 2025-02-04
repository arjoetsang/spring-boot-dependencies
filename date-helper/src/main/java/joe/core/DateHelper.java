package joe.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateHelper {
    private final DateTimeFormatter defaultFormatter;

    public DateHelper(DateTimeFormatter defaultFormatter) {
        this.defaultFormatter = defaultFormatter;
    }

    public String format(LocalDateTime dateTime) {
        return defaultFormatter.format(dateTime);
    }

    public String format(LocalDateTime dateTime, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(dateTime);
    }

    public LocalDateTime parse(String dateString) {
        return LocalDateTime.parse(dateString, defaultFormatter);
    }

    public LocalDateTime parse(String dateString, String pattern) {
        return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(pattern));
    }
}