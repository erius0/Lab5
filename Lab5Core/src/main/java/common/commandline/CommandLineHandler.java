package common.commandline;

import common.commandline.response.CommandResult;
import common.commandline.response.DefaultResponse;
import common.commandline.response.Response;
import common.util.UtilFunctions;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Класс обработчика командной строки, реализует шаблон проектирования Singleton,
 * читает ввод с командной строки, обрабатывает его и вызывает соответствующую команду
 * на выполнение из регистра команд, используйте метод {@link #start()} для его запуска
 *
 * @see CommandRegistry
 */
public abstract class CommandLineHandler {

    @Setter @Getter
    private static String user = "lab7";
    protected static CommandLineHandler instance;

    protected final Deque<Reader> inputs = new LinkedList<>();
    protected final Deque<String> fileNames = new LinkedList<>();
    protected final List<String> history = new LinkedList<>();
    protected BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    protected boolean isActive = false;

    {
        CommandRegistry.registerCommands(new ExitCommand(), new HistoryCommand(), new ExecuteScriptCommand());
    }

    public static CommandLineHandler getInstance() {
        if (instance == null) throw new CommandLineNotInitialized("Обработчик командной строки не инициализирован");
        return instance;
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

    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Что-то пошло не так");
        }
    }

    protected void input() {
        String line = awaitInput(user + ">").toLowerCase(Locale.ROOT);
        String[] split = line.split("\\s+");
        String alias = split[0];
        String[] args = new String[]{};
        if (split.length > 1) {
            args = new String[split.length - 1];
            System.arraycopy(split, 1, args, 0, split.length - 1);
        }
        executeCommand(alias, args);
    }

    protected abstract void executeCommand(String alias, String[] args);

    protected void updateHistory(String command) {
        history.add(command);
    }

    public boolean isActive() {
        return isActive;
    }

    public String awaitInput(String msg) {
        return awaitInput(msg, "Что-то пошло не так");
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

    public String awaitPassword(String msg) {
        Console console = System.console();
        if (console == null) {
            System.err.println("Не удалось получить консоль");
            System.exit(-1);
        }
        do {
            System.out.print(msg + " ");
            char[] input = console.readPassword();
            if (input == null || input.length == 0) {
                System.err.println("Пароль не может быть пустым");
                continue;
            }
            return new String(input);
        } while (true);
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

    public class ExitCommand extends Command {
        public ExitCommand() {
            super("exit", true, "exit : завершить программу (без сохранения в файл)");
        }

        @Override
        public CommandResult execute(Object[] args) {
            isActive = false;
            return new CommandResult("Выход из программы...", DefaultResponse.OK);
        }
    }

    public class HistoryCommand extends Command {
        public HistoryCommand() {
            super("history", true, "history [count] : вывести последние count введенных команд, по умолчанию count равен 6");
        }

        @Override
        public Object[] validate(String[] args) {
            Integer value = 6;
            Object[] newArgs = new Object[]{ value };
            if (args.length > 0) {
                value = UtilFunctions.intOrNull(args[0]);
                if (value != null) {
                    newArgs = new Object[]{ value };
                    return newArgs;
                } else return null;
            }
            return newArgs;
        }

        @Override
        public CommandResult execute(Object[] args) {
            int lines = args.length > 0 ? (int) args[0] : 6;
            int start = lines < history.size() ? history.size() - lines : 0;
            StringBuilder result = new StringBuilder("История последних команд:\n");
            for (int i = start; i < history.size(); i++)
                result.append(history.get(i)).append("\n");
            return new CommandResult(result.toString(), DefaultResponse.OK);
        }
    }

    public class ExecuteScriptCommand extends Command {
        public ExecuteScriptCommand() {
            super("execute_script", true, "execute_script {file_name} : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.");
        }

        @Override
        public Object[] validate(String[] args) {
            if (args.length < 1) {
                System.err.println("Недостаточно данных");
                return null;
            }
            return args;
        }

        @Override
        public CommandResult execute(Object[] args) {
            String fileName = (String) args[0];
            File file = new File(fileName);
            if (!file.exists() || file.isDirectory()) {
                Response response = DefaultResponse.FILE_NOT_FOUND;
                return new CommandResult(response.getMsg(), response);
            }

            Reader streamReader;
            try {
                streamReader = new InputStreamReader(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                Response response = DefaultResponse.UNKNOWN;
                return new CommandResult(response.getMsg(), response);
            }
            addNewInput(streamReader, fileName);
            Response response = DefaultResponse.OK;
            return new CommandResult(response.getMsg(), response);
        }
    }

    public static class CommandLineNotInitialized extends RuntimeException {
        public CommandLineNotInitialized() {
            super();
        }

        public CommandLineNotInitialized(String message) {
            super(message);
        }
    }
}
