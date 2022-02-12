package ru.erius.lab5.collection;

import java.util.Locale;

/**
 * Класс данных координат
 */
public class Coordinates {

    /**
     * Координата X типа float
     */
    private float x;
    /**
     * Координата Y типа float, значение должно быть больше -816
     */
    private float y;

    /**
     * Конструктор без параметров, задаёт значения всех полей по умолчанию,
     * x = 0, y = 0
     */
    public Coordinates() {
        this.x = 0F;
        this.y = 0F;
    }

    /**
     * Конструктор с параметрами
     *
     * @param x Координата X
     * @param y Координата Y
     *
     * @throws IllegalArgumentException Если Y меньше или равен -816
     */
    public Coordinates(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    /**
     * Сеттер для поля y
     *
     * @param y Координата Y
     *
     * @throws IllegalArgumentException Если Y меньше или равен -816
     */
    public void setY(float y) {
        this.y = y;
        if (y <= -816)
            throw new IllegalArgumentException("Поле y класса Coordinates должно быть больше -816");
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Coordinates that = (Coordinates) other;

        if (Float.compare(that.x, x) != 0) return false;
        return Float.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int result = (x != 0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != 0.0f ? Float.floatToIntBits(y) : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "Coordinates(x=%f, y=%f)", this.x, this.y);
    }
}
