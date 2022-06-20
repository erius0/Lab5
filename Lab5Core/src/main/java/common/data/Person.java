package common.data;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

/**
 * Класс данных человека, реализует сортировку по умолчанию по имени, номеру паспорта,
 * росту, национальности, местоположению и цвету глаз
 */
@Data @EqualsAndHashCode @ToString
public class Person implements Comparable<Person>, Serializable {
    /**
     * Id человека, не может быть null, значение поля должно быть больше 0,
     * значение этого поля должно быть уникальным, значение этого поля должно генерироваться автоматически
     */
    private Long id;
    /**
     * Имя человека, не может быть null, строка не может быть пустой
     */
    private String name;
    /**
     * Координаты человека, не может быть null
     */
    private Coordinates coordinates;
    /**
     * Дата создания объекта, не может быть null, значение этого поля должно генерироваться автоматически
     */
    private LocalDate creationDate;
    /**
     * Рост человека, может быть null, значение поля должно быть больше 0
     */
    private Integer height;
    /**
     * Номер паспорта человека, длина строки должна быть не меньше 8, поле может быть null
     */
    private String passportID;
    /**
     * Цвет глаз человека, не может быть null
     */
    private Color eyeColor;
    /**
     * Национальность человека, не может быть null
     */
    private Country nationality;
    /**
     * Местоположение человека, может быть null
     */
    private Location location;
    private String owner;

    /**
     * Конструктор с параметрами
     *
     * @param id Id человека
     * @param name Имя человека
     * @param coordinates Координаты человека
     * @param height Высота человека
     * @param passportID Номер паспорта человека
     * @param eyeColor Цвет глаз человека
     * @param nationality Национальность человека
     * @param location Местоположение человека
     * @param owner Создатель человека
     *
     * @throws NullPointerException Если name, coordinates, eyeColor или nationality являются null
     */
    public Person(long id, @NonNull String name, @NonNull Coordinates coordinates, Integer height, String passportID,
                  @NonNull Color eyeColor, @NonNull Country nationality, Location location, String owner) {
        this.id = id;
        this.creationDate = LocalDate.now();
        this.location = location;
        this.coordinates = coordinates;
        this.eyeColor = eyeColor;
        this.nationality = nationality;
        this.name = name;
        this.height = height;
        this.passportID = passportID;
        this.owner = owner;
    }

    public Person(@NonNull String name, @NonNull Coordinates coordinates, Integer height, String passportID,
                  @NonNull Color eyeColor, @NonNull Country nationality, Location location, String owner) {
        this(0, name, coordinates, height, passportID, eyeColor, nationality, location, owner);
    }

    /**
     * Метод, меняющий все значения полей человека (кроме {@link #id} и {@link #creationDate}) в соответствии
     * со значениями полей другого
     *
     * @param newPerson человек, чьи поля будут присвоены текущему
     */
    public void update(Person newPerson) {
        this.location = newPerson.location;
        this.coordinates = newPerson.coordinates;
        this.eyeColor = newPerson.eyeColor;
        this.nationality = newPerson.nationality;
        this.setName(newPerson.name);
        this.setHeight(newPerson.height);
        this.setPassportID(newPerson.passportID);
    }

    public String formatted() {
        return String.format("Человек %s:\n" +
                        "\tИмя: %s\n" +
                        "\tДата создания: %s\n" +
                        "\tРост: %s\n" +
                        "\tНомер паспорта: %s\n" +
                        "\tЦвет глаз: %s\n" +
                        "\tНациональность: %s\n" +
                        "\tМестоположение:\n" +
                        "\t\tНазвание: %s\n" +
                        "\t\tX: %s\n" +
                        "\t\tY: %s\n" +
                        "\t\tZ: %s\n" +
                        "\tКоординаты:\n" +
                        "\t\tX: %s\n" +
                        "\t\tY: %s\n" +
                        "\tСоздатель: %s\n",
                        id, name, creationDate, height, passportID, eyeColor, nationality,
                        location.getName(), location.getX(), location.getY(), location.getZ(),
                        coordinates.getX(), coordinates.getY(), owner);
    }

    /**
     * Переопределенный метод сравнения двух людей,
     * сравнение производится по имени, номеру паспорта,
     * росту, национальности, местоположению и цвету глаз
     *
     * @param other Объект для сравнения
     * @return Целое число - результат сравнения
     */
    @Override
    public int compareTo(Person other) {
        return Comparator.comparing(Person::getName)
                .thenComparing(Person::getPassportID, Comparator.nullsFirst(String::compareTo))
                .thenComparing(Person::getHeight, Comparator.nullsFirst(Integer::compareTo))
                .thenComparing(Person::getCreationDate)
                .thenComparing(p -> p.getNationality().toString())
                .thenComparing(Person::getLocation)
                .thenComparing(Person::getCoordinates)
                .thenComparing(p -> p.getEyeColor().toString())
                .thenComparing(Person::getId)
                .compare(this, other);
    }
}
