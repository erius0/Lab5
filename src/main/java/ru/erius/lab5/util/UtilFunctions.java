package ru.erius.lab5.util;

public class UtilFunctions {

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
}
