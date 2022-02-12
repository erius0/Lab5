package ru.erius.lab5.collection;

/**
 * Перечисление стран
 */
public enum Country {
    UNITED_KINGDOM,
    GERMANY,
    CHINA,
    THAILAND,
    JAPAN;

    /**
     * Метод, проверяющий, можно ли из данной строки получить значение типа Country
     *
     * @param strCountry Проверяемая строка
     *
     * @return true, если из строки можно получить значение типа Country, иначе false
     */
    public static boolean isCountryValid(String strCountry) {
        try {
            Country.valueOf(strCountry);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
