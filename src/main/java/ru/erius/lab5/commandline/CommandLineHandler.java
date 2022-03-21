package ru.erius.lab5.commandline;

import ru.erius.lab5.util.UtilFunctions;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Класс обработчика командной строки, реализует шаблон проектирования Singleton,
 * читает ввод с командной строки, обрабатывает его и вызывает соответствующую команду
 * на выполнение из регистра команд, используйте метод {@link #start()} для его запуска
 *
 * @see CommandRegistry
 */
public final class CommandLineHandler {

    private final static CommandLineHandler instance = new CommandLineHandler();

    private final Deque<Reader> inputs = new LinkedList<>();
    private final Deque<String> fileNames = new LinkedList<>();
    private final List<String> history = new LinkedList<>();
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private boolean isActive = false;

    static {
        clearScreen();
        CommandLineHandler.registerBasicCommands();
    }

    private CommandLineHandler() {}

    public static CommandLineHandler getInstance() {
        return instance;
    }

    private static void registerBasicCommands() {
        CommandRegistry.registerCommand("exit", args -> instance.exit(),
                "exit : завершить программу (без сохранения в файл)");
        CommandRegistry.registerCommand("history", instance::showHistory,
                "history [count] : вывести последние count введенных команд, по умолчанию count равен 6");
        CommandRegistry.registerCommand("execute_script", instance::executeScript,
                "execute_script {file_name} : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.");
    }

    /**
     * Метод, запускающий обработчик командной строки, для остановки требуется ввести команду "exit"
     */
    public void start() {
        System.out.println(LongStrings.GREETINGS.getValue());
        this.isActive = true;
        do {
            input();
        } while (this.isActive);
    }

    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void input() {
        String line = awaitInput("lab5>", "Что-то пошло не так").toLowerCase(Locale.ROOT);
        String[] split = line.split("\\s+");
        String alias = split[0];
        String[] args = new String[]{};
        if (split.length > 1) {
            args = new String[split.length - 1];
            System.arraycopy(split, 1, args, 0, split.length - 1);
        }
        Command command = CommandRegistry.getCommand(alias);
        if (command == null) {
            System.out.println("Неизвестная команда " + alias + ", напишите help для отображения всех существующих команд");
            return;
        }
        command.execute(args);
        updateHistory(alias);
    }

    /**
     * Метод, останавливающий работу обработчика командной строки
     */
    public void exit() {
        this.isActive = false;
        System.out.println("Выход из программы...");
    }

    public void showHistory(String[] args) {
        int lines = 6;
        if (args.length >= 1 && UtilFunctions.intOrNull(args[0]) != null)
            lines = Integer.parseInt(args[0]);
        if (lines <= 0) {
            System.out.println("[count] должен быть больше 0");
            return;
        }
        int start = lines < history.size() ? history.size() - lines : 0;
        System.out.println("История последних команд:");
        for (int i = start; i < history.size(); i++)
            System.out.println(history.get(i));
    }

    public void executeScript(String[] args) {
        if (args.length < 1) {
            System.out.println("Недостаточно данных");
            return;
        }

        String fileName = args[0];
        File file = new File(fileName);
        if (!file.exists() || file.isDirectory()) {
            System.out.println("Не удалось найти файл " + fileName);
            return;
        }

        Reader streamReader;
        try {
            streamReader = new InputStreamReader(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Что-то пошло не так");
            return;
        }
        addNewInput(streamReader, fileName);
    }

    private void updateHistory(String command) {
        history.add(command);
    }

    /**
     * Метод, ожидающий ввода из потока ввода {@link #reader reader} и возвращающий результат,
     * печатает запрос msg перед ожиданием данных (если их вводит пользователь),
     * печатает ошибку err, если при вводе данных произошла ошибка
     *
     * @param msg Строка, печатающаяся как запрос данных от пользователя
     * @param err Строка, печатающаяся во время ошибки
     *
     * @return Строка из потока ввода
     */
    public String awaitInput(String msg, String err) {
        return awaitInput(msg, err, input -> true);
    }

    /**
     * Метод, ожидающий ввода из потока ввода {@link #reader reader } и возвращающий результат,
     * печатает запрос msg перед ожиданием данных (если их вводит пользователь),
     * печатает ошибку err, если введенные данные не соответствуют предикату predicate
     *
     * @param msg Строка, печатающаяся как запрос данных от пользователя
     * @param err Строка, печатающаяся при несоответствии ввода предикату
     * @param predicate Предикат, определяющий валидность введенных данных
     *
     * @return Строка из потока ввода
     */
    public String awaitInput(String msg, String err, Predicate<String> predicate) {
        String input = null;
        do {
            if (inputs.isEmpty())
                System.out.print(msg + " ");
            try {
                input = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (input == null) {
                removeInput();
                continue;
            }
            input = input.trim();
            if (predicate.test(input))
                break;
            else
                System.err.println(err);
        } while (true);
        System.out.println();
        return input;
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
    public <T> T awaitInput(String msg, String err, Predicate<String> predicate, Function<String, T> transform) {
        String result = awaitInput(msg, err, predicate);
        return transform.apply(result);
    }

    public void addNewInput(Reader reader, String filePath) {
        if (this.fileNames.contains(filePath)) {
            System.err.println("Замечена рекурсия, отмена смены потока");
            return;
        }
        this.fileNames.add(filePath);
        this.reader = new BufferedReader(reader);
        this.inputs.add(reader);
    }

    public void removeInput() {
        if (fileNames.size() > 0)
            fileNames.removeLast();
        inputs.poll();
        Reader reader = inputs.isEmpty() ? new InputStreamReader(System.in) : inputs.peek();
        this.reader = new BufferedReader(reader);
    }
}
