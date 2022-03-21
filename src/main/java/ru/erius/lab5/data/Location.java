package ru.erius.lab5.data;

import lombok.*;
import ru.erius.lab5.parser.Adapters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Comparator;

/**
 * Класс данных местоположения, реализует сортировку по умолчанию
 * по имени и расстоянию до точки (0; 0; 0)
 */
@Data @EqualsAndHashCode @ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class Location implements Comparable<Location> {

    /**
     * Координата X типа double
     */
    private double x;
    /**
     * Координата Y типа float
     */
    private float y;
    /**
     * Координата Z типа Long, не может быть null
     */
    private Long z;
    /**
     * Имя локации, может быть null
     */
    @XmlElement(nillable = true)
    @XmlJavaTypeAdapter(Adapters.NameAdapter.class)
    private String name;

    private Location() {
        this.setName(this.name);
    }

    /**
     * Конструктор с параметрами
     *
     * @param x Координата X
     * @param y Координата Y
     * @param z Координата Z
     * @param name Имя локации
     *
     * @throws IllegalArgumentException будет брошено, если name является пустой строкой
     *
     * @throws NullPointerException будет брошено в случае, если Z является null
     */
    public Location(double x, float y, @NonNull Long z, String name) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.setName(name);
    }

    /**
     * Сеттер для поля name
     * @param name Имя локации
     *
     * @throws IllegalArgumentException Если name является пустой строкой
     */
    public void setName(String name) {
        this.name = name;
        if (name != null && name.isEmpty())
            this.name = null;
    }

    /**
     * Переопределенный метод сравнения двух местоположений,
     * сравнение производится по имени локации и расстоянию до точки (0; 0; 0)
     *
     * @param other Объект для сравнения
     * @return Целое число - результат сравнения
     */
    @Override
    public int compareTo(Location other) {
        return Comparator.comparing(Location::getName, Comparator.nullsFirst(String::compareTo))
                .thenComparing(Location::distance)
                .compare(this, other);
    }

    /**
     * Метод, вычисляющий расстояние до точки (0; 0; 0)
     *
     * @return Расстояние типа double
     */
    private double distance() {
        return Math.sqrt(x * x + y * y + z * z);
    }
}
