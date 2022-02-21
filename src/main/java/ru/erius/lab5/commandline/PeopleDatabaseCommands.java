package ru.erius.lab5.commandline;

import ru.erius.lab5.collection.Database;
import ru.erius.lab5.collection.PeopleDatabase;
import ru.erius.lab5.data.*;
import ru.erius.lab5.util.UtilFunctions;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

import static ru.erius.lab5.commandline.CommandRegistry.*;

/**
 * Класс объявления всех команд, связанных с манипуляциями над объектом класса PeopleDatabase
 *
 * @see PeopleDatabase
 */
public final class PeopleDatabaseCommands {

    private static PeopleDatabase peopleDatabase;
    private static final String COLORS = Arrays.toString(Color.values()),
                                COUNTRIES = Arrays.toString(Country.values());
    private static final CommandLineHandler CMD = CommandLineHandler.getInstance();

    private PeopleDatabaseCommands() {}

    /**
     * Метод добавления всех команд в регистр команд, обязательно вызывайте его в своей программе,
     * если вам требуются данные команды
     */
    public static void registerDatabaseCommands() {
        registerCommand("info", args -> info(peopleDatabase),
                "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
        registerCommand("show", args -> show(peopleDatabase),
                "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
        registerCommand("add", args -> add(peopleDatabase),
                "add <Person> : добавить новый элемент в коллекцию");
        registerCommand("update", args -> update(peopleDatabase, args),
                "update {id} <Person> : обновить значение элемента коллекции, {id} которого равен заданному");
        registerCommand("remove_by_id", args -> remove(peopleDatabase, args),
                "remove_by_id {id} : удалить элемент из коллекции по его {id}");
        registerCommand("clear", args -> clear(peopleDatabase),
                "clear : очистить коллекцию");
        registerCommand("save", args -> save(peopleDatabase),
                "save : сохранить коллекцию в файл");
        registerCommand("add_if_max", args -> addIfMax(peopleDatabase),
                "add_if_max <Person> : добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции");
        registerCommand("add_if_min", args -> addIfMin(peopleDatabase),
                "add_if_min <Person> : добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции");
        registerCommand("sum_of_height", args -> sumOfHeight(peopleDatabase),
                "sum_of_height : вывести сумму значений поля height для всех элементов коллекции");
        registerCommand("filter_contains_name", args -> filterContainsName(peopleDatabase, args),
                "filter_contains_name {name} : вывести элементы, значение поля name которых содержит заданную подстроку");
        registerCommand("print_field_descending_location", args -> printFieldDescendingLocation(peopleDatabase),
                "print_field_descending_location : вывести значения поля location всех элементов в порядке убывания");
    }

    public static void setPeopleDatabase(PeopleDatabase peopleDatabase) {
        PeopleDatabaseCommands.peopleDatabase = peopleDatabase;
    }

    public static void info(PeopleDatabase peopleDatabase) {
        System.out.println(peopleDatabase.info());
    }

    public static void show(PeopleDatabase peopleDatabase) {
        System.out.println(peopleDatabase);
    }

    public static void add(PeopleDatabase peopleDatabase) {
        Person person = createPerson();
        boolean success = peopleDatabase.getCollection().add(person);
        System.out.println(success ? "Человек успешно добавлен в коллекцию" : "Не удалось добавить человека в коллекцию");
    }

    public static void update(PeopleDatabase peopleDatabase, String[] args) {
        Long id = getIdOrNull(args);
        if (id == null) return;

        Optional<Person> optionalPerson = peopleDatabase.getCollection()
                .stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
        if (!optionalPerson.isPresent()) {
            System.out.println("Человек с {id} " + id + " не был найден");
            return;
        }
        Person oldPerson = optionalPerson.get();
        peopleDatabase.getCollection().remove(oldPerson);

        Person newPerson = createPerson();
        oldPerson.update(newPerson);
        peopleDatabase.getCollection().add(oldPerson);
        System.out.println("Человек с {id} " + oldPerson.getId() + " был успешно изменен");
    }

    public static void remove(PeopleDatabase peopleDatabase, String[] args) {
        Long id = getIdOrNull(args);
        if (id == null) return;
        boolean success = peopleDatabase.getCollection().removeIf(p -> p.getId().equals(id));
        System.out.println("Человек с {id} " + id + " " + (success ?  "был успешно удален" :  "не был найден"));
    }

    public static void clear(PeopleDatabase peopleDatabase) {
        peopleDatabase.getCollection().clear();
        System.out.println("Коллекция была очищена");
    }

    public static void save(PeopleDatabase peopleDatabase) {
        try {
            peopleDatabase.save();
            System.out.println("Коллекция была успешно сохранена");
        } catch (Database.DatabaseSaveFailedException e) {
            e.printStackTrace();
            System.out.println("Не удалось сохранить коллекцию");
        }
    }

    public static void addIfMax(PeopleDatabase peopleDatabase) {
        Person person = createPerson();
        Person last = peopleDatabase.getCollection().last();
        if (person.compareTo(last) > 0) {
            peopleDatabase.getCollection().add(person);
            System.out.println("Человек успешно добавлен в коллекцию");
            return;
        }
        System.out.println("Не удалось добавить человека в коллекцию");
    }

    public static void addIfMin(PeopleDatabase peopleDatabase) {
        Person person = createPerson();
        Person first = peopleDatabase.getCollection().first();
        if (person.compareTo(first) < 0) {
            peopleDatabase.getCollection().add(person);
            System.out.println("Человек успешно добавлен в коллекцию");
            return;
        }
        System.out.println("Не удалось добавить человека в коллекцию");
    }

    public static void sumOfHeight(PeopleDatabase peopleDatabase) {
        int sum = peopleDatabase.getCollection()
                .stream()
                .mapToInt(p -> p.getHeight() == null ? 0 : p.getHeight())
                .sum();
        System.out.println("Сумма ростов всех людей в коллекции - " + sum);
    }

    public static void filterContainsName(PeopleDatabase peopleDatabase, String[] args) {
        if (args.length < 1) {
            System.out.println("Недостаточно данных");
            return;
        }

        String name = args[0];
        System.out.println("Список людей, в имени которых содержится " + name + ":");
        peopleDatabase.getCollection()
                .stream()
                .filter(p -> p.getName().contains(name))
                .forEach(System.out::println);
    }

    public static void printFieldDescendingLocation(PeopleDatabase peopleDatabase) {
        System.out.println("Список локаций в порядке убывания");
        peopleDatabase.getCollection()
                .stream()
                .map(Person::getLocation)
                .sorted(Collections.reverseOrder())
                .forEach(System.out::println);
    }

    private static Long getIdOrNull(String[] args) {
        if (args.length < 1) {
            System.out.println("Недостаточно данных");
            return null;
        }

        Long id = UtilFunctions.longOrNull(args[0]);
        if (id == null) {
            System.out.println("{id} должен быть целым числом");
            return null;
        }

        return id;
    }

    public static Person createPerson() {
        System.out.println("Создание нового объекта класса Person");
        String name = CMD.awaitInput("Введите имя:", "Введите непустую строку",
                input -> !input.isEmpty());
        Integer height = CMD.awaitInput("Введите рост:", "Введите целое число, большее нуля",
                input -> {
                    Integer result = UtilFunctions.intOrNull(input);
                    return result != null && result > 0 || input.isEmpty();
                }, input -> input.isEmpty() ? null : Integer.parseInt(input));
        String passportID = CMD.awaitInput("Введите номер паспорта:", "Введите минимум 8 символов",
                input -> input.length() >= 8 || input.isEmpty(), input -> input.isEmpty() ? null : input);
        Color eyeColor = CMD.awaitInput("Введите цвет глаз " + COLORS + ":", "Введите один из предложенных цветов",
                input -> UtilFunctions.enumOrNull(input.toUpperCase(Locale.ROOT), Color.class) != null,
                input -> Color.valueOf(input.toUpperCase(Locale.ROOT)));
        Country nationality = CMD.awaitInput("Введите национальность " + COUNTRIES + ":", "Введите одну из предложенных стран",
                input -> UtilFunctions.enumOrNull(input.toUpperCase(Locale.ROOT), Country.class) != null,
                input -> Country.valueOf(input.toUpperCase(Locale.ROOT)));
        Location location = createLocation();
        Coordinates coordinates = createCoordinates();
        return new Person(name, coordinates, height, passportID, eyeColor, nationality, location);
    }

    public static Location createLocation() {
        System.out.println("Создание нового объекта класса Location");
        double x = CMD.awaitInput("Введите x:", "Введите дробное число",
                input -> UtilFunctions.doubleOrNull(input) != null, Double::parseDouble);
        float y = CMD.awaitInput("Введите y:", "Введите дробное число",
                input -> UtilFunctions.floatOrNull(input) != null, Float::parseFloat);
        long z = CMD.awaitInput("Введите z:", "Введите целое число",
                input -> UtilFunctions.longOrNull(input) != null, Long::parseLong);
        String name = CMD.awaitInput("Введите название:", "Строка не может быть пустой",
                input -> true, input -> input.isEmpty() ? null : input);
        return new Location(x, y, z, name);
    }

    public static Coordinates createCoordinates() {
        System.out.println("Создание нового объекта класса Coordinates");
        float x = CMD.awaitInput("Введите x:", "Введите дробное число",
                input -> UtilFunctions.floatOrNull(input) != null, Float::parseFloat);
        float y = CMD.awaitInput("Введите y:", "Введите дробное число, большее -816",
                input -> {
                    Float result = UtilFunctions.floatOrNull(input);
                    return result != null && result > -816F;
                }, Float::parseFloat);
        return new Coordinates(x, y);
    }
}
