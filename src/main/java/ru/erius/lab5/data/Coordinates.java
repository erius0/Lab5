package ru.erius.lab5.data;

import lombok.*;
import ru.erius.lab5.parser.Adapters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
    @XmlJavaTypeAdapter(Adapters.CoordinateYAdapter.class)
    private Float y;

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
            this.y = 0F;
    }

    private double distance() {
        return Math.sqrt(x * x + y * y);
    }

    @Override
    public int compareTo(Coordinates other) {
        return Double.compare(this.distance(), other.distance());
    }
}
