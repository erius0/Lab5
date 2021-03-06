package common.commandline.pdcommands;

import common.commandline.CommandLineHandler;
import common.collection.PeopleCollection;
import common.data.*;
import common.util.UtilFunctions;

import java.util.Arrays;
import java.util.Locale;

/**
 * Класс объявления всех команд, связанных с манипуляциями над объектом класса PeopleDatabase
 *
 * @see PeopleCollection
 */
public final class PeopleDatabaseCommands {

    public static PeopleCollection peopleCollection;
    private static final String COLORS = Arrays.toString(Color.values()),
                                COUNTRIES = Arrays.toString(Country.values());
    private static final CommandLineHandler CMD = CommandLineHandler.getInstance();

    private PeopleDatabaseCommands() {}

    public static Person createPerson() {
        System.out.println("Создание нового объекта класса Person");
        String name = CMD.awaitInput("Введите имя:", "Введите 1-50 символов",
                input -> !input.isEmpty() && input.length() <= 50);
        Integer height = CMD.awaitInput("Введите рост:", "Введите целое число в диапазоне [1; 2 147 483 647] или ничего",
                input -> {
                    Integer result = UtilFunctions.intOrNull(input);
                    return result != null && result > 0 || input.isEmpty();
                }, input -> input.isEmpty() ? null : Integer.parseInt(input));
        String passportID = CMD.awaitInput("Введите номер паспорта:", "Введите 8-50 символов или ничего",
                input -> (input.length() >= 8 && input.length() <= 50) || input.isEmpty(), input -> input.isEmpty() ? null : input);
        Color eyeColor = CMD.awaitInput("Введите цвет глаз " + COLORS + ":", "Введите один из предложенных цветов",
                input -> UtilFunctions.enumOrNull(input.toUpperCase(Locale.ROOT), Color.class) != null,
                input -> Color.valueOf(input.toUpperCase(Locale.ROOT)));
        Country nationality = CMD.awaitInput("Введите национальность " + COUNTRIES + ":", "Введите одну из предложенных стран",
                input -> UtilFunctions.enumOrNull(input.toUpperCase(Locale.ROOT), Country.class) != null,
                input -> Country.valueOf(input.toUpperCase(Locale.ROOT)));
        Location location = createLocation();
        Coordinates coordinates = createCoordinates();
        return new Person(name, coordinates, height, passportID, eyeColor, nationality, location, CommandLineHandler.getUser());
    }

    public static Location createLocation() {
        System.out.println("Создание нового объекта класса Location");
        double x = CMD.awaitInput("Введите x:", "Введите дробное число типа double",
                input -> UtilFunctions.doubleOrNull(input) != null, Double::parseDouble);
        float y = CMD.awaitInput("Введите y:", "Введите дробное число типа float",
                input -> UtilFunctions.floatOrNull(input) != null, Float::parseFloat);
        long z = CMD.awaitInput("Введите z:", "Введите целое число типа long",
                input -> UtilFunctions.longOrNull(input) != null, Long::parseLong);
        String name = CMD.awaitInput("Введите название:", "Введите 1-50 символов или ничего",
                input -> input.length() <= 50, input -> input.isEmpty() ? null : input);
        return new Location(x, y, z, name);
    }

    public static Coordinates createCoordinates() {
        System.out.println("Создание нового объекта класса Coordinates");
        float x = CMD.awaitInput("Введите x:", "Введите дробное число типа float",
                input -> UtilFunctions.floatOrNull(input) != null, Float::parseFloat);
        float y = CMD.awaitInput("Введите y:", "Введите дробное число типа float, большее -816",
                input -> {
                    Float result = UtilFunctions.floatOrNull(input);
                    return result != null && result > -816F;
                }, Float::parseFloat);
        return new Coordinates(x, y);
    }
}
