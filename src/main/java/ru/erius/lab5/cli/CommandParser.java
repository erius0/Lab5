package ru.erius.lab5.cli;

import ru.erius.lab5.collection.*;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Класс, добавляющий возможность интерактивного взаимодействия пользователя с коллекцией через терминал,
 * для своей работы требует экземпляр класса {@link PersonTreeSet PersonTreeSet}
 */
public class CommandParser {
    /**
     * Вспомогательная строка для создания красивого вывода
     */
    private final static String LINE = "=============================================================================================================================================================================================";
    /**
     * Строка, выводимая при запуске программы
     */
    private final static String GREETINGS = LINE + "\n" +
            "Добро пожаловать в программу для управления коллекцией объектов в интерактивном режиме!\n" +
            "Напишите help, чтобы увидеть доступные команды\n" +
            "Напишите exit, чтобы выйти из программы\n" +
            LINE + "\n";
    /**
     * Строка, выводимая при написании команды help
     */
    private final static String HELP = LINE + "\n" +
            "help: вывести справку по доступным командам\n" +
            "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
            "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
            "add [element] : добавить новый элемент в коллекцию\n" +
            "update {id} [element] : обновить значение элемента коллекции, id которого равен заданному\n" +
            "remove_by_id {id} : удалить элемент из коллекции по его id\n" +
            "clear : очистить коллекцию\n" +
            "save : сохранить коллекцию в файл\n" +
            "execute_script {file_name} : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n" +
            "exit : завершить программу (без сохранения в файл)\n" +
            "add_if_max [element] : добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции\n" +
            "add_if_min [element] : добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции\n" +
            "history : вывести последние 6 команд (без их аргументов)\n" +
            "sum_of_height : вывести сумму значений поля height для всех элементов коллекции\n" +
            "filter_contains_name {name} : вывести элементы, значение поля name которых содержит заданную подстроку\n" +
            "print_field_descending_location : вывести значения поля location всех элементов в порядке убывания\n" +
            LINE + "\n";
    /**
     * Коллекция, на основе которой функционирует данный класс
     */
    private final PersonTreeSet personTreeSet;
    /**
     * Логическая переменная, хранящая состояние программы
     */
    private boolean isActive = false;
    /**
     * Очередь входных потоков, используется для выполнения пользовательских скриптов,
     * также требуется для реализации возможности рекурсивного выполнения скриптов
     */
    private final Queue<InputStream> inputStreams = new LinkedList<>();
    /**
     * Reader, использующийся для чтения пользовательских команд или скриптов
     */
    private BufferedReader reader;
    /**
     * Список последних 6 выполненных команд
     */
    private final LinkedList<String> history = new LinkedList<>();

    /**
     * Конструктор класса
     *
     * @param personTreeSet Коллекция типа {@link PersonTreeSet PersonTreeSet}
     */
    public CommandParser(PersonTreeSet personTreeSet) {
        this.personTreeSet = personTreeSet;
    }

    /**
     * Метод, открывающий поток ввода данных и запускающий программу,
     * циклически вызывая функцию {@link #parse() parse}
     */
    public void start() {
        System.out.println(GREETINGS);
        reader = new BufferedReader(new InputStreamReader(System.in));
        this.isActive = true;
        do {
            parse();
        } while (this.isActive);
    }

    /**
     * Основной метод, который ждет от пользователя ввода данных, после чего
     * делит строку на команду и её аргументы, и в зависимости от них,
     * вызывает нужную функцию на выполнение
     */
    public void parse() {
        String input = awaitInput("", "Что-то пошло не так").toLowerCase(Locale.ROOT);
        String[] split = input.split(" ");
        String command = split[0], argument = split.length > 1 ? split[1] : null;
        switch (command) {
            case "help": help(); break;
            case "info": info(); break;
            case "show": show(); break;
            case "add": add(); break;
            case "update": update(argument); break;
            case "remove_by_id": removeByID(argument); break;
            case "clear": clear(); break;
            case "save": save(); break;
            case "execute_script": executeScript(argument); break;
            case "exit": exit(); break;
            case "add_if_max": addIfMax(); break;
            case "add_if_min": addIfMin(); break;
            case "history": history(); break;
            case "sum_of_height": sumOfHeight(); break;
            case "filter_contains_name": filterContainsName(argument); break;
            case "print_field_descending_location": printFieldDescendingLocation(); break;
            default: unknown(); return;
        }
        updateHistory(command);
    }

    /**
     * Метод, обновляющий историю введенных команд
     *
     * @param command Введенная команда
     */
    private void updateHistory(String command) {
        history.add(command);
        if (this.history.size() > 6)
            this.history.removeFirst();
    }

    /**
     * Метод, вызываемый при вводе неизвестной команды
     */
    public void unknown() {
        System.err.println("Неизвестная команда. Введите help для отображения списка всех команд");
    }

    /**
     * Метод, вызываемый при вводе команды help,
     * печатает содержимое строки {@link #HELP HELP}
     */
    public void help() {
        System.out.println(CommandParser.HELP);
    }

    /**
     * Метод, вызываемый при вводе команды info,
     * печатает информацию о коллекции
     */
    public void info() {
        System.out.println(this.personTreeSet.info());
    }

    /**
     * Метод, вызываемый при вводе команды show,
     * печатает содержимое коллекции
     */
    public void show() {
        System.out.println("Вся коллекция PersonTreeSet:");
        System.out.println(this.personTreeSet.toString());
    }

    /**
     * Метод, вызываемый при вводе команды add,
     * пытается добавить новый элемент в коллекцию,
     * печатает результат добавления
     */
    public void add() {
        Person person = createPerson();
        boolean success = this.personTreeSet.add(person);
        String msg = success ? "Объект был добавлен в коллекцию" : "Не удалось добавить объект";
        System.out.println(msg);
    }

    /**
     * Метод, вызываемый при вводе команды update,
     * пытается изменить существующий элемент коллекции по заданному id
     * печатает результат изменения
     *
     * @param id Id человека
     */
    public void update(long id) {
        if (Person.getExistingPeople() < id || id <= 0) {
            System.err.println("Не удалось найти объект с id " + id);
            return;
        }
        Person person = createPerson();
        boolean success = this.personTreeSet.update(id, person);
        String msg = success ? "Объект был успешно изменен" : "Не удалось найти объект с id " + id;
        System.out.println(msg);
    }

    /**
     * Метод, аналогичный {@link #update(long) update(long)},
     * пытается преобразовать данную строку в Long и вызвать {@link #update(long) update(long)}
     *
     * @param strId Id человека типа String
     */
    public void update(String strId) {
        Long id = idOrNull(strId);
        if (id != null)
            update(id);
    }

    /**
     * Метод, вызываемый при вводе команды remove_by_id,
     * пытается удалить существующий элемент из коллекции по заданному id
     * печатает результат изменения
     *
     * @param id Id человека
     */
    public void removeByID(long id) {
        boolean success = this.personTreeSet.remove(id);
        String msg = success ? "Объект был удален" : "Не удалось найти объект с id " + id;
        System.out.println(msg);
    }

    /**
     * Метод, аналогичный {@link #removeByID(long)  update(long)},
     * пытается преобразовать данную строку в Long и вызвать {@link #update(long) update(long)}
     *
     * @param strId Id человека типа String
     */
    public void removeByID(String strId) {
        Long id = idOrNull(strId);
        if (id != null)
            removeByID(id);
    }

    /**
     * Метод, преобразующий строковый id в Long,
     * печатает результат преобразования
     *
     * @param strId Преобразуемая строка
     * @return Преобразованная строка в Long или null при ошибке
     */
    public Long idOrNull(String strId) {
        long id;
        if (strId == null) {
            System.err.println("Параметр id должен быть указан");
            return null;
        }
        try {
            id = Long.parseLong(strId);
        } catch (NumberFormatException e) {
            System.err.println("Параметр id должен являться целым числом");
            return null;
        }
        return id;
    }

    /**
     * Метод, вызываемый при вводе команды clear, очищает коллекцию
     */
    public void clear() {
        this.personTreeSet.clear();
        System.out.println("Коллекция успешно очищена");
    }

    /**
     * Метод, вызываемый при вводе команды save,
     * пытается сохранить коллекцию в файл,
     * печатает результат сохранения
     */
    public void save() {
        boolean success = this.personTreeSet.save();
        String msg = success ? "Коллекция была сохранена в файле " + this.personTreeSet.getFile().getAbsolutePath()
                : "Не удалось сохранить коллекцию (обычно причина пишется при запуске программы)";
        System.out.println(msg);
    }

    /**
     * Метод, вызываемый при вводе команды execute_script,
     * пытается выполнить скрипт, написанный в файле fileName,
     * печатает результат выполнения скрипта
     *
     * @param fileName Имя или путь к файлу
     */
    public void executeScript(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            System.err.println("Параметр file_name должен быть указан");
            return;
        }
        File file = new File(fileName);
        if (!file.exists() || file.isDirectory()) {
            System.err.println("Не удалось найти указанный файл со скриптом");
            return;
        }
        InputStream stream;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.err.println("Файл не найден");
            return;
        }
        newInputStream(stream);
    }

    /**
     * Метод, вызываемый при вводе команды exit,
     * заканчивает выполнение программы, предлагая перед этим сохранить коллекцию
     */
    public void exit() {
        if (this.personTreeSet.getFile() != null) {
            String answer = awaitInput("Сохранить коллекцию в файл? (Y - да, N - нет)", "Введите Y или N",
                    input -> input.toUpperCase(Locale.ROOT).equals("Y") || input.toUpperCase(Locale.ROOT).equals("N"),
                    input -> input.toUpperCase(Locale.ROOT));
            if (answer.equals("Y"))
                save();
        }
        System.out.println("Выход из программы...");
        this.isActive = false;
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод, вызываемый при вводе команды add_if_max,
     * пытается добавить новый элемент в коллекцию, только если он больше максимального элемента в коллекции
     * печатает результат добавления
     */
    public void addIfMax() {
        Person person = createPerson();
        boolean success = this.personTreeSet.addIfMax(person);
        String msg = success ? "Объект был добавлен в коллекцию" : "Не удалось добавить объект";
        System.out.println(msg);
    }

    /**
     * Метод, вызываемый при вводе команды add_if_min,
     * пытается добавить новый элемент в коллекцию, только если он меньше минимального элемента в коллекции
     * печатает результат добавления
     */
    public void addIfMin() {
        Person person = createPerson();
        boolean success = this.personTreeSet.addIfMin(person);
        String msg = success ? "Объект был добавлен в коллекцию" : "Не удалось добавить объект";
        System.out.println(msg);
    }

    /**
     * Метод, вызываемый при вводе команды history,
     * печатает историю последних 6 введенных команд
     */
    public void history() {
        System.out.println("История последних 6 команд:");
        this.history.forEach(System.out::println);
    }

    /**
     * Метод, вызываемый при вводе команды sum_of_height,
     * печатает суммарный рост всех людей в коллекции
     */
    public void sumOfHeight() {
        int sum = this.personTreeSet.sumOfHeight();
        System.out.println("Сумма ростов всех людей в коллекции: " + sum);
    }

    /**
     * Метод, фильтрующий и печатающий всех людей, в имени которых имеется строка name,
     * проверка не чувствительна к регистру
     *
     * @param name Строка, по которой происходит фильтрация
     */
    public void filterContainsName(String name) {
        if (name == null || name.isEmpty()) {
            System.err.println("Параметр name должен быть указан");
            return;
        }
        List<Person> people = this.personTreeSet.filterContainsName(name);
        System.out.println("Список людей, в имени которых содержится " + name + ":");
        people.forEach(System.out::println);
    }

    /**
     * Метод, печатающий отсортированный по убыванию список местоположений всех людей в коллекции
     */
    public void printFieldDescendingLocation() {
        List<Location> locations = this.personTreeSet.fieldDescendingLocation();
        System.out.println("Список локаций в порядке убывания:");
        locations.forEach(System.out::println);
    }

    /**
     * Метод, интерактивно создающий нового человека, получая каждое новое значение из потока ввода {@link #reader reader}
     *
     * @return Новый экземпляр класса {@link Person Person}
     */
    private Person createPerson() {
        System.out.println("Создание нового объекта класса Person");
        Person person = new Person();
        person.setName(
                awaitInput("Введите имя:", "Введите непустую строку",
                        input -> !input.isEmpty())
        );
        person.setHeight(
                awaitInput("Введите рост:", "Введите целое число, большее нуля",
                        input -> {
                            try {
                                int num = Integer.parseInt(input);
                                return num > 0;
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        }, Integer::parseInt)
        );
        person.setPassportID(
                awaitInput("Введите номер паспорта:", "Введите как минимум 8 символов",
                        input -> input.length() >= 8)
        );
        person.setEyeColor(
                awaitInput("Введите цвет глаз (BLACK, ORANGE, BROWN):", "Введите один из предложенных цветов",
                        input -> Color.isColorValid(input.toUpperCase(Locale.ROOT)), input -> Color.valueOf(input.toUpperCase(Locale.ROOT)))
        );
        person.setNationality(
                awaitInput("Введите страну (UNITED_KINGDOM, GERMANY, CHINA, THAILAND, JAPAN):", "Введите одну из предлженных стран",
                        input -> Country.isCountryValid(input.toUpperCase(Locale.ROOT)), input -> Country.valueOf(input.toUpperCase(Locale.ROOT)))
        );
        person.setCoordinates(createCoordinates());
        person.setLocation(createLocation());
        return person;
    }

    /**
     * Метод, интерактивно создающий новые координаты, получая каждое новое значение из потока ввода {@link #reader reader}
     *
     * @return Новый экземпляр класса {@link Coordinates Coordinates}
     */
    private Coordinates createCoordinates() {
        System.out.println("Создание нового объекта класса Coordinates");
        Coordinates coordinates = new Coordinates();
        coordinates.setX(
                awaitInput("Введите x:", "Введите дробное число",
                        input -> {
                            try {
                                Float.parseFloat(input);
                                return true;
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        }, Float::parseFloat)
        );
        coordinates.setY(
                awaitInput("Введите y:", "Введите дробное число, большее -816",
                        input -> {
                            try {
                                float num = Float.parseFloat(input);
                                return num > -816F;
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        }, Float::parseFloat)
        );
        return coordinates;
    }

    /**
     * Метод, интерактивно создающий новое местоположение, получая каждое новое значение из потока ввода {@link #reader reader}
     *
     * @return Новый экземпляр класса {@link Location Location}
     */
    private Location createLocation() {
        System.out.println("Создание нового объекта класса Location");
        Location location = new Location();
        location.setX(
                awaitInput("Введите x:", "Введите дробное число",
                        input -> {
                            try {
                                Double.parseDouble(input);
                                return true;
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        }, Double::parseDouble)
        );
        location.setY(
                awaitInput("Введите y:", "Введите дробное число",
                        input -> {
                            try {
                                Double.parseDouble(input);
                                return true;
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        }, Float::parseFloat)
        );
        location.setZ(
                awaitInput("Введите z:", "Введите целое число",
                        input -> {
                            try {
                                Long.parseLong(input);
                                return true;
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        }, Long::parseLong)
        );
        location.setName(
                awaitInput("Введите название:", "Введите непустую строку",
                        input -> !input.isEmpty())
        );
        return location;
    }

    /**
     * Метод, ожидающий ввода из потока ввода {@link #reader reader} и возвращающий результат,,
     * печатает запрос msg перед ожиданием данных (если их вводит пользователь),
     * печатает ошибку err, если при вводе данных произошла ошибка
     *
     * @param msg Строка, печатающаяся как запрос данных от пользователя
     * @param err Строка, печатающаяся во время ошибки
     *
     * @return Строка из потока ввода
     */
    private String awaitInput(String msg, String err) {
        return awaitInput(msg, err, input -> true);
    }

    /**
     * Метод, ожидающий ввода из потока ввода {@link #reader reader } и возвращающий результат,,
     * печатает запрос msg перед ожиданием данных (если их вводит пользователь),
     * печатает ошибку err, если введенные данные не соответствуют предикату predicate
     *
     * @param msg Строка, печатающаяся как запрос данных от пользователя
     * @param err Строка, печатающаяся при несоответствии ввода предикату
     * @param predicate Предикат, определяющий валидность введенных данных
     *
     * @return Строка из потока ввода
     */
    private String awaitInput(String msg, String err, Predicate<String> predicate) {
        String input = null;
        do {
            if (inputStreams.isEmpty())
                System.out.print(msg + "\n>>> ");
            try {
                input = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (input == null) {
                prevInputStream();
                continue;
            }
            if (predicate.test(input.trim()))
                break;
            else
                System.err.println(err);
        } while (true);
        System.out.println();
        return input.trim();
    }

    /**
     * Метод, ожидающий ввода из потока ввода {@link #reader reader} и возвращающий результат,
     * печатает запрос msg перед ожиданием данных (если их вводит пользователь),
     * печатает ошибку err, если введенные данные не соответствуют предикату predicate,
     * преобразует результат в тип T в соответствии с функцией transform
     *
     * @param msg Строка, печатающаяся как запрос данных от пользователя
     * @param err Строка, печатающаяся при несоответствии ввода предикату
     * @param predicate Предикат, определяющий валидность введенных данных
     * @param <T> Тип, к которому будет приведен результат
     * @param transform Функция, преобразующая результат в тип T
     *
     * @return Результат типа T
     */
    private <T> T awaitInput(String msg, String err, Predicate<String> predicate, Function<String, T> transform) {
        String result = awaitInput(msg, err, predicate);
        return transform.apply(result);
    }

    /**
     * Метод, меняющий текущий поток ввода на stream и добавляет его в очередь {@link #inputStreams inputStreams}
     *
     * @param stream Новый поток ввода
     */
    private void newInputStream(InputStream stream) {
        this.reader = new BufferedReader(new InputStreamReader(stream));
        this.inputStreams.add(stream);
    }

    /**
     * Метод, убирающий текущий поток ввода из очереди {@link #inputStreams inputStreams}
     * и меняющий его либо на следующий в очереди поток, либо на System.in, если очередь пуста
     */
    private void prevInputStream() {
        inputStreams.poll();
        InputStream stream = inputStreams.isEmpty() ? System.in : inputStreams.peek();
        this.reader = new BufferedReader(new InputStreamReader(stream));
    }
}
