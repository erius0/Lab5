package ru.erius.lab5.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import ru.erius.lab5.parser.LocalDateAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.*;

/**
 * Класс данных человека, реализует сортировку по умолчанию по имени, номеру паспорта,
 * росту, национальности, местоположению и цвету глаз
 */
@Data @EqualsAndHashCode @ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Person implements Comparable<Person> {
    /**
     * Количество созданных людей, используется для задания
     * уникального id для каждого объекта данного класса
     */
    @XmlTransient
    private static long existingPeople = 0;

    /**
     * Id человека, не может быть null, значение поля должно быть больше 0,
     * значение этого поля должно быть уникальным, значение этого поля должно генерироваться автоматически
     */
    @XmlTransient
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
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
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

    /**
     * Конструктор для корректного преобразования человека из xml в объект
     */
    private Person() {
        this.id = ++existingPeople;
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
     * @throws IllegalArgumentException Если:
     * name является пустой строкой,
     * height меньше 0,
     * Длина passportID меньше 8 символов
     *
     * @throws NullPointerException Если coordinates, eyeColor или nationality являются null
     */
    public Person(String name, @NonNull Coordinates coordinates, Integer height, String passportID,
                  @NonNull Color eyeColor, @NonNull Country nationality, Location location) {
        this.id = ++existingPeople;
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

    /**
     * Сеттер для поля name
     *
     * @param name
     * Имя человека
     *
     * @throws IllegalArgumentException
     * Если имя является пустой строкой
     */
    public void setName(String name) {
        this.name = name;
        if (name.isEmpty())
            throw new IllegalArgumentException("Поле name класса Person не может быть null или пустым");
    }

    /**
     * Сеттер для поля height
     *
     * @param height Рост человека
     *
     * @throws IllegalArgumentException Если рост меньше 0
     */
    public void setHeight(Integer height) {
        this.height = height;
        if (height <= 0)
            throw new IllegalArgumentException("Поле height класса Person должно быть больше 0");
    }

    /**
     * Сеттер для поля passportID
     *
     * @param passportID Номер паспорта человека
     *
     * @throws IllegalArgumentException Если номер паспорта меньше 8 символов в длину
     */
    public void setPassportID(String passportID) {
        this.passportID = passportID;
        if (passportID.length() < 8)
            throw new IllegalArgumentException("Поле passportID класса Person не может быть меньше 8 символов в длину");
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
        return Comparator.comparing((Person p) -> p.name)
                .thenComparing(p -> p.passportID)
                .thenComparing(p -> p.height)
                .thenComparing(p -> p.nationality)
                .thenComparing(p -> p.location)
                .thenComparing(p -> p.eyeColor)
                .compare(this, other);
    }
}
