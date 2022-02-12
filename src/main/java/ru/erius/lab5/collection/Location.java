package ru.erius.lab5.collection;

import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

/**
 * Класс данных местоположения, реализует сортировку по умолчанию
 * по имени и расстоянию до точки (0; 0; 0)
 */
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
     * Конструктор без параметров, задаёт значения всех полей по умолчанию,
     * x = 0, y = 0, z = 0, name = null
     */
    public Location() {
        this.x = 0D;
        this.y = 0F;
        this.z = 0L;
        this.name = null;
    }

    /**
     * Конструктор с параметрами
     *
     * @param x Координата X
     * @param y Координата Y
     * @param z Координата Z
     * @param name Имя локации
     *
     * @throws IllegalArgumentException Данное исключение будет брошено в следующих случаях:
     * Z является null,
     * name является пустой строкой
     */
    public Location(double x, float y, Long z, String name) {
        this.x = x;
        this.y = y;
        this.setZ(z);
        this.setName(name);
    }

    public double getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Long getZ() {
        return z;
    }

    public String getName() {
        return name;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    /**
     * Сеттер для поля z
     * @param z Координата Z
     *
     * @throws IllegalArgumentException Если Z является null
     */
    public void setZ(Long z) {
        this.z = z;
        if (z == null)
            throw new IllegalArgumentException("Поле z класса Location не может быть null");
    }

    /**
     * Сеттер для поля name
     * @param name Имя локации
     *
     * @throws IllegalArgumentException Если name является пустой строкой
     */
    public void setName(String name) {
        this.name = name;
        if (name.isEmpty())
            throw new IllegalArgumentException("Поле name класса Location не может быть пустым");
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
        return Comparator.comparing(Location::getName)
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

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Location location = (Location) other;

        if (Double.compare(location.x, x) != 0) return false;
        if (Float.compare(location.y, y) != 0) return false;
        if (!z.equals(location.z)) return false;
        return Objects.equals(name, location.name);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (y != 0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + z.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "Location(x=%f, y=%f, z=%s, name=%s)", this.x, this.y, this.z, this.name);
    }
}
