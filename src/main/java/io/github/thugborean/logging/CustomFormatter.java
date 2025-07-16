package io.github.thugborean.logging;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CustomFormatter extends Formatter{

    private static final DateTimeFormatter timeStamp =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    // This is straight voodoo
    @Override
    public String format(LogRecord record) {
        String loggerSimpleName = "unknown";
        String fullName = record.getLoggerName();
        if (fullName != null && fullName.contains(".")) {
            String[] parts = fullName.split("\\.");
            loggerSimpleName = parts[parts.length - 1].toLowerCase();
            if (loggerSimpleName.endsWith("visitor")) {
                loggerSimpleName = loggerSimpleName.replace("visitor", "");
            }
        }

        String time = timeStamp.format(Instant.ofEpochMilli(record.getMillis()));
        String level = record.getLevel().getName();
        return String.format("[%s] %-7s [%s] %s%n", time, level, loggerSimpleName, formatMessage(record));
    }
}