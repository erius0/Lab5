package ru.erius.lab5.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Класс данных координат
 */
@Data @NoArgsConstructor @EqualsAndHashCode @ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class Coordinates implements Comparable<Coordinates> {

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
        if (y <= -816)
            throw new IllegalArgumentException("Поле y класса Coordinates должно быть больше -816");
        this.y = y;
    }

    private double distance() {
        return Math.sqrt(x * x + y * y);
    }

    @Override
    public int compareTo(Coordinates other) {
        return Double.compare(this.distance(), other.distance());
    }
}
