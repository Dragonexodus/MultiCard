package helpers;


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

    public static void log(Exception ex) {
        logInternal(LogLevel.ERROR, "%s: %s", ex.getClass().getName(), ex.getLocalizedMessage());
    }

    private static void logInternal(LogLevel level, String message, Object... args) {
        String m = String.format(message, args);
        String logMessage = String.format("%s [%s]: %s", ZonedDateTime.now().toLocalTime(), level.toString(), m);

        System.out.println(logMessage);
        if (onNewLogEntry != null) {
            onNewLogEntry.accept(logMessage);
        }
    }

    public static void setOnNewLogEntry(Consumer<String> onNewLogEntry) {
        LogHelper.onNewLogEntry = onNewLogEntry;
    }
}
