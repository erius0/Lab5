package common.util;

import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Класс функций-утилит для избавления от повторяющихся участков кода и выноса их в методы для общего пользования
 */
public final class UtilFunctions {

    private UtilFunctions() {}

    public static Integer intOrNull(String number) {
        int result;
        try {
            result = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return null;
        }
        return result;
    }

    public static Long longOrNull(String number) {
        long result;
        try {
            result = Long.parseLong(number);
        } catch (NumberFormatException e) {
            return null;
        }
        return result;
    }

    public static Float floatOrNull(String number) {
        float result;
        try {
            result = Float.parseFloat(number);
        } catch (NumberFormatException e) {
            return null;
        }
        return result;
    }

    public static Double doubleOrNull(String number) {
        double result;
        try {
            result = Double.parseDouble(number);
        } catch (NumberFormatException e) {
            return null;
        }
        return result;
    }

    public static <T extends Enum<T>> T enumOrNull(String value, Class<T> enumType) {
        try {
            return T.valueOf(enumType, value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static Logger getLogger(Class<?> clazz, String mainLoggerName) {
        changeLoggerFormat(mainLoggerName);
        return Logger.getLogger(clazz.getName());
    }

    private static void changeLoggerFormat(String mainLoggerName) {
        Logger mainLogger = Logger.getLogger(mainLoggerName);
        mainLogger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {
            private static final String format = "[%1$tF %1$tT] [%2$s] %3$s %n";

            @Override
            public synchronized String format(LogRecord record) {
                return String.format(format,
                        new Date(record.getMillis()),
                        record.getLevel().getLocalizedName(),
                        record.getMessage()
                );
            }
        });
        mainLogger.addHandler(handler);
    }
}
