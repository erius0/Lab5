package ru.erius.lab5.collection;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс данных человека, реализует сортировку по умолчанию по имени, номеру паспорта,
 * росту, национальности, местоположению и цвету глаз
 */
public class Person implements Comparable<Person> {

    /**
     * Количество созданных людей, используется для задания
     * уникального id для каждого объекта данного класса
     */
    private static long existingPeople = 0;

    /**
     * Регулярное выражение для валидации и создания
     * экземпляров данного класса из строк
     */
    private final static Pattern PERSON_REGEX = Pattern.compile(
                    "Person\\(\\s*id=(?<id>\\d+),\\s*name=(?<name>\\S+)," +
                    "\\s*coordinates=Coordinates\\(x=(?<coordX>[+-]?(\\d+([.,]\\d*)?|[.,]\\d+)),\\s*y=(?<coordY>[+-]?(\\d+([.,]\\d*)?|[.,]\\d+))\\)," +
                    "\\s*creationDate=(?<creationDate>\\d{4}[\\-.]\\d{2}[\\-.]\\d{2}),\\s*height=(?<height>\\d+),\\s*passportID=(?<passportID>\\S{8,})," +
                    "\\s*eyeColor=(?<eyeColor>\\w+),\\s*nationality=(?<nationality>\\w+),\\" +
                    "s*location=Location\\(x=(?<locX>[+-]?(\\d+([.,]\\d*)?|[.,]\\d+)),\\s*y=(?<locY>[+-]?(\\d+([.,]\\d*)?|[.,]\\d+))," +
                    "\\s*z=(?<locZ>[+-]?\\d+),\\s*name=(?<locName>\\S+)\\)\\)"
    );

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

    /**
     * Конструктор без параметров, задаёт значения всех полей по умолчанию,
     * name = "None", coordinates = new Coordinates(), height = 1,
     * passportID = null, eyeColor = Color.BLACK, nationality = Country.UNITED_KINGDOM
     */
    public Person() {
        this.id = ++existingPeople;
        this.creationDate = LocalDate.now();
        this.location = null;
        this.name = "None";
        this.coordinates = new Coordinates();
        this.height = 1;
        this.passportID = null;
        this.eyeColor = Color.BLACK;
        this.nationality = Country.UNITED_KINGDOM;
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
     * @throws IllegalArgumentException Данное исключение будет брошено в следующих случаях:
     * name является null или пустой строкой,
     * coordinates является null,
     * height меньше 0,
     * Длина passportID меньше 8 символов,
     * eyeColor является null,
     * nationality является null
     */
    public Person(String name, Coordinates coordinates, Integer height, String passportID,
                  Color eyeColor, Country nationality, Location location) {
        this.id = ++existingPeople;
        this.creationDate = LocalDate.now();
        this.location = location;
        this.setName(name);
        this.setCoordinates(coordinates);
        this.setHeight(height);
        this.setPassportID(passportID);
        this.setEyeColor(eyeColor);
        this.setNationality(nationality);
    }

    public static long getExistingPeople() {
        return existingPeople;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Integer getHeight() {
        return height;
    }

    public String getPassportID() {
        return passportID;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    public Country getNationality() {
        return nationality;
    }

    public Location getLocation() {
        return location;
    }

    /**
     * Сеттер для поля name
     *
     * @param name Имя человека
     *
     * @throws IllegalArgumentException Если имя является null или пустой строкой
     */
    public void setName(String name) {
        this.name = name;
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Поле name класса Person не может быть null или пустым");
    }

    /**
     * Сеттер для поля coordinates
     *
     * @param coordinates Координаты человека
     *
     * @throws IllegalArgumentException Если объект координат является null
     */
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
        if (coordinates == null)
            throw new IllegalArgumentException("Поле coordinates класса Person не может быть null");
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
     * Сеттер для поля eyeColor
     *
     * @param eyeColor Координаты человека
     *
     * @throws IllegalArgumentException Если цвет глаз является null
     */
    public void setEyeColor(Color eyeColor) {
        this.eyeColor = eyeColor;
        if (eyeColor == null)
            throw new IllegalArgumentException("Поле eyeColor класса Person не может быть null");
    }

    /**
     * Сеттер для поля nationality
     *
     * @param nationality Координаты человека
     *
     * @throws IllegalArgumentException Если национальность является null
     */
    public void setNationality(Country nationality) {
        this.nationality = nationality;
        if (nationality == null)
            throw new IllegalArgumentException("Поле nationality класса Person не может быть null");
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Статический метод, возвращающий список объектов Person,
     * образованный из найденных в строке совпадений с
     * регулярным выражением {@link #PERSON_REGEX PERSON_REGEX}
     *
     * @param str Строка, из которой требуется получить список людей
     * @return Список объектов класса Person
     */
    public static List<Person> peopleFromString(String str) {
        List<Person> list = new ArrayList<>();
        Matcher matcher = PERSON_REGEX.matcher(str);
        while (matcher.find()) {
            try {
                Person person = new Person();
                person.id = Long.parseLong(matcher.group("id"));
                person.name = matcher.group("name");
                person.height = Integer.parseInt(matcher.group("height"));
                person.passportID = matcher.group("passportID");
                person.eyeColor = Color.valueOf(matcher.group("eyeColor"));
                person.nationality = Country.valueOf(matcher.group("nationality"));
                person.coordinates = new Coordinates(
                        Float.parseFloat(matcher.group("coordX")),
                        Float.parseFloat(matcher.group("coordY"))
                );
                person.location = new Location(
                        Double.parseDouble(matcher.group("locX")),
                        Float.parseFloat(matcher.group("locY")),
                        Long.parseLong(matcher.group("locZ")),
                        matcher.group("locName")
                );
                String[] date = matcher.group("creationDate").split("[\\-.]");
                person.creationDate = LocalDate.of(
                        Integer.parseInt(date[0]),
                        Integer.parseInt(date[1]),
                        Integer.parseInt(date[2])
                );
                list.add(person);
            } catch (NumberFormatException | DateTimeException e) {
                e.printStackTrace();
                System.err.println("Что-то пошло не так при чтении данных из файла, не меняйте вручную данные в файле!!!");
            }
        }
        return list;
    }

    /**
     * Переопределенный метод сравнения двух людей,
     * сравнение производится по имени, номеру пасспорта,
     * росту, национальности, местоположению и цвету глаз
     *
     * @param other Объект для сравнения
     * @return Целое число - результат сравнения
     */
    @Override
    public int compareTo(Person other) {
        return Comparator.comparing(Person::getName)
                .thenComparing(Person::getPassportID)
                .thenComparing(Person::getHeight)
                .thenComparing(Person::getNationality)
                .thenComparing(Person::getLocation)
                .thenComparing(Person::getEyeColor)
                .compare(this, other);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Person person = (Person) other;

        if (!name.equals(person.name)) return false;
        if (!coordinates.equals(person.coordinates)) return false;
        if (!Objects.equals(height, person.height)) return false;
        if (!Objects.equals(passportID, person.passportID)) return false;
        if (eyeColor != person.eyeColor) return false;
        if (nationality != person.nationality) return false;
        return Objects.equals(location, person.location);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + coordinates.hashCode();
        result = 31 * result + (height != null ? height.hashCode() : 0);
        result = 31 * result + (passportID != null ? passportID.hashCode() : 0);
        result = 31 * result + eyeColor.hashCode();
        result = 31 * result + nationality.hashCode();
        result = 31 * result + (location != null ? location.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "Person(id=%d, name=%s, coordinates=%s, creationDate=%s, height=%s, passportID=%s, " +
                "eyeColor=%s, nationality=%s, location=%s)", this.id, this.name, this.coordinates, this.creationDate,
                this.height, this.passportID, this.eyeColor, this.nationality, this.location);
    }
}
