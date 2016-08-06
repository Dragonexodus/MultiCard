package helper;


import java.time.ZonedDateTime;
import java.util.function.Consumer;

/**
 * Created by Patrick on 22.06.2015.
 */

public class LogHelper {

    private static Consumer<String> onNewLogEntry = null;

    public static void log(LogLevel level, String message, Object... args) {
        logInternal(level, message, args);
    }

    public static void log(Exception e) {
        logInternal(LogLevel.ERROR, "%s: %s", e.getClass().getName(), e.getLocalizedMessage());
    }

    private static void logInternal(LogLevel level, String message, Object... args) {
        String s = String.format(message, args);
        String logMsg = String.format("%s [%s]: %s", ZonedDateTime.now().toLocalTime(), level.toString(), s);

        System.out.println(logMsg);
        if (onNewLogEntry != null) {
            onNewLogEntry.accept(logMsg);
        }
    }

    public static void setOnNewLogEntry(Consumer<String> onNewLogEntry) {
        LogHelper.onNewLogEntry = onNewLogEntry;
    }
}
