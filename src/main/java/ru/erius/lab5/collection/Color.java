package ru.erius.lab5.collection;

/**
 * Перечисление цветов
 */
public enum Color {
    BLACK,
    ORANGE,
    BROWN;

    /**
     * Метод, проверяющий, можно ли из данной строки получить значение типа Color
     *
     * @param strColor Проверяемая строка
     *
     * @return true, если из строки можно получить значение типа Color, иначе false
     */
    public static boolean isColorValid(String strColor) {
        try {
            Color.valueOf(strColor);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
