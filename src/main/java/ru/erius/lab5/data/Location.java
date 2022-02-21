package ru.erius.lab5.data;

import lombok.*;

import java.util.Comparator;

/**
 * Класс данных местоположения, реализует сортировку по умолчанию
 * по имени и расстоянию до точки (0; 0; 0)
 */
@Data @NoArgsConstructor @EqualsAndHashCode @ToString
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
    private String name;

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
        if (name != null && name.isEmpty())
            throw new IllegalArgumentException("Поле name класса Location не может быть пустым");
        this.name = name;
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
        return Comparator.comparing((Location loc) -> loc.name)
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
