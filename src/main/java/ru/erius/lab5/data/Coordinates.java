package ru.erius.lab5.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Класс данных координат
 */
@Data @NoArgsConstructor @EqualsAndHashCode @ToString
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
     * Конструктор с параметрами
     *
     * @param x Координата X
     * @param y Координата Y
     *
     * @throws IllegalArgumentException Если Y меньше или равен -816
     */
    public Coordinates(float x, float y) {
        this.x = x;
        this.setY(y);
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
}
