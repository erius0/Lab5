package common.data;

import lombok.*;
import common.parser.Adapters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;

/**
 * Класс данных координат
 */
@Data @EqualsAndHashCode @ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class Coordinates implements Comparable<Coordinates>, Serializable {

    /**
     * Координата X типа float
     */
    private float x;
    /**
     * Координата Y типа float, значение должно быть больше -816
     */
    @XmlJavaTypeAdapter(Adapters.CoordinateYAdapter.class)
    private Float y;

    private Coordinates() {
        this.x = Adapters.DEFAULT_COORDINATE;
        this.y = (float) Adapters.DEFAULT_COORDINATE;
    }

    /**
     * Конструктор с параметрами
     *
     * @param x Координата X
     * @param y Координата Y
     */
    public Coordinates(float x, float y) {
        this.x = x;
        this.setY(y);
    }

    /**
     * Сеттер для поля y
     *
     * @param y Координата Y
     */
    public void setY(float y) {
        this.y = y;
        if (y <= -816)
            this.y = (float) Adapters.DEFAULT_COORDINATE;
    }

    private double distance() {
        return Math.sqrt(x * x + y * y);
    }

    @Override
    public int compareTo(Coordinates other) {
        return Double.compare(this.distance(), other.distance());
    }
}
