package common.data;

import lombok.*;

import java.io.Serializable;

/**
 * Класс данных координат
 */
@Data @EqualsAndHashCode @ToString
public class Coordinates implements Comparable<Coordinates>, Serializable {

    /**
     * Координата X типа float
     */
    private float x;
    /**
     * Координата Y типа float, значение должно быть больше -816
     */
    private Float y;

    /**
     * Конструктор с параметрами
     *
     * @param x Координата X
     * @param y Координата Y
     */
    public Coordinates(float x, float y) {
        this.x = x;
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
