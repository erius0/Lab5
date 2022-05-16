package common.data;

import lombok.*;
import common.parser.Adapters;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

/**
 * Класс данных человека, реализует сортировку по умолчанию по имени, номеру паспорта,
 * росту, национальности, местоположению и цвету глаз
 */
@Data @EqualsAndHashCode @ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Person implements Comparable<Person>, Serializable {
    /**
     * Количество созданных людей, используется для задания
     * уникального id для каждого объекта данного класса
     */
    @XmlTransient @Getter
    private static long existingPeople = 0;
    /**
     * Id человека, не может быть null, значение поля должно быть больше 0,
     * значение этого поля должно быть уникальным, значение этого поля должно генерироваться автоматически
     */
    @XmlTransient @Setter
    private Long id;
    /**
     * Имя человека, не может быть null, строка не может быть пустой
     */
    @XmlJavaTypeAdapter(Adapters.NameAdapter.class)
    private String name;
    /**
     * Координаты человека, не может быть null
     */
    private Coordinates coordinates;
    /**
     * Дата создания объекта, не может быть null, значение этого поля должно генерироваться автоматически
     */
    @XmlJavaTypeAdapter(Adapters.LocalDateAdapter.class)
    private LocalDate creationDate;
    /**
     * Рост человека, может быть null, значение поля должно быть больше 0
     */
    @XmlElement(nillable = true)
    @XmlJavaTypeAdapter(Adapters.HeightAdapter.class)
    private Integer height;
    /**
     * Номер паспорта человека, длина строки должна быть не меньше 8, поле может быть null
     */
    @XmlElement(nillable = true)
    @XmlJavaTypeAdapter(Adapters.PassportAdapter.class)
    private String passportID;
    /**
     * Цвет глаз человека, не может быть null
     */
    @XmlJavaTypeAdapter(Adapters.ColorAdapter.class)
    private Color eyeColor;
    /**
     * Национальность человека, не может быть null
     */
    @XmlJavaTypeAdapter(Adapters.CountryAdapter.class)
    private Country nationality;
    /**
     * Местоположение человека, может быть null
     */
    private Location location;

    /**
     * Конструктор для корректного преобразования человека из xml в объект
     */
    private Person() {
        this.id = ++existingPeople;
        this.creationDate = Adapters.DEFAULT_DATE;
        this.name = Adapters.DEFAULT_NAME;
        this.coordinates = Adapters.DEFAULT_COORDINATES;
        this.height = Adapters.DEFAULT_HEIGHT;
        this.passportID = Adapters.DEFAULT_PASSPORT;
        this.eyeColor = Adapters.DEFAULT_COLOR;
        this.nationality = Adapters.DEFAULT_COUNTRY;
        this.location = Adapters.DEFAULT_LOCATION;
    }

    /**
     * Конструктор с параметрами
     *
     * @param name Имя человека
     * @param coordinates Координаты человека
     * @param height Высота человека
     * @param passportID Номер паспорта человека
     * @param eyeColor Цвет глаз человека
     * @param nationality Национальность человека
     * @param location Местоположение человека
     *
     * @throws NullPointerException Если name, coordinates, eyeColor или nationality являются null
     */
    public Person(@NonNull String name, @NonNull Coordinates coordinates, Integer height, String passportID,
                  @NonNull Color eyeColor, @NonNull Country nationality, Location location) {
        this.id = existingPeople;
        this.creationDate = LocalDate.now();
        this.location = location;
        this.coordinates = coordinates;
        this.eyeColor = eyeColor;
        this.nationality = nationality;
        this.setName(name);
        this.setHeight(height);
        this.setPassportID(passportID);
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

    public static void incrementExistingPeople() {
        existingPeople++;
    }

    /**
     * Сеттер для поля name
     *
     * @param name
     * Имя человека
     */
    public void setName(String name) {
        this.name = name;
        if (name.isEmpty())
            this.name = Adapters.DEFAULT_NAME;
    }

    /**
     * Сеттер для поля height
     *
     * @param height Рост человека
     */
    public void setHeight(Integer height) {
        this.height = height;
        if (height != null && height <= 0)
            this.height = Adapters.DEFAULT_HEIGHT;
    }

    /**
     * Сеттер для поля passportID
     *
     * @param passportID Номер паспорта человека
     */
    public void setPassportID(String passportID) {
        this.passportID = passportID;
        if (passportID != null && passportID.length() < 8)
            this.passportID = Adapters.DEFAULT_PASSPORT;
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
                        "\t\tY: %s\n",
                        id, name, creationDate, height, passportID, eyeColor, nationality,
                        location.getName(), location.getX(), location.getY(), location.getZ(),
                        coordinates.getX(), coordinates.getY());
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
