package client.commandline.pdcommands;

import client.commandline.CommandLineHandler;
import client.commandline.CommandRegistry;
import common.collection.PeopleDatabase;
import common.data.*;
import common.util.UtilFunctions;

import java.util.Arrays;
import java.util.Locale;

/**
 * Класс объявления всех команд, связанных с манипуляциями над объектом класса PeopleDatabase
 *
 * @see PeopleDatabase
 */
public final class PeopleDatabaseCommands {

    public static PeopleDatabase peopleDatabase;
    private static final String COLORS = Arrays.toString(Color.values()),
                                COUNTRIES = Arrays.toString(Country.values());
    private static final CommandLineHandler CMD = CommandLineHandler.getInstance();

    private PeopleDatabaseCommands() {}

    /**
     * Метод добавления всех команд в регистр команд, обязательно вызывайте его в своей программе,
     * если вам требуются данные команды
     */
    public static void registerDatabaseCommands() {
        CommandRegistry.registerCommands(new InfoCommand(), new ShowCommand(), new AddCommand(), new AddIfMaxCommand(),
                new AddIfMinCommand(), new ClearCommand(), new FilterContainsNameCommand(), new PrintFieldDescendingLocationCommand(),
                new RemoveByIdCommand(), new SaveCommand(), new SumOfHeightCommand(), new UpdateCommand());
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
