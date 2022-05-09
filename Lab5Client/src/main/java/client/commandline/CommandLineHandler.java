package client.commandline;

import client.net.UDPClient;
import common.commandline.response.CommandResult;
import common.commandline.response.DefaultResponse;
import common.parser.ConnectionProperties;
import common.util.UtilFunctions;

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
public final class CommandLineHandler {

    private final static CommandLineHandler instance = new CommandLineHandler();

    private final UDPClient udp = new UDPClient(ConnectionProperties.getHostname(), ConnectionProperties.getPort());
    private final Deque<Reader> inputs = new LinkedList<>();
    private final Deque<String> fileNames = new LinkedList<>();
    private final List<String> history = new LinkedList<>();
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private boolean isActive = false;
    private boolean clientMode = true;

    static {
        clearScreen();
        CommandLineHandler.registerBasicCommands();
    }

    private CommandLineHandler() {}

    public static CommandLineHandler getInstance() {
        return instance;
    }

    private static void registerBasicCommands() {
        CommandRegistry.registerCommands(new ExitCommand(), new HistoryCommand(), new ExecuteScriptCommand(), new ModeCommand());
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
        executeCommand(alias, args);
    }

    private void executeCommand(String alias, String[] args) {
        Command command = CommandRegistry.getCommand(alias);
        if (command == null) {
            System.err.println("Неизвестная команда " + alias + ", напишите help для отображения всех существующих команд");
            return;
        }
        boolean argsValid = command.validate(args);
        if (!argsValid) return;
        CommandResult result = command.isClientOnly() || clientMode ?
                command.executeOnClient() : executeOnServer(udp, command);
        PrintStream ps = result.getResponse() == DefaultResponse.OK ? System.out : System.err;
        if (result.getValue() == null) ps.println(result.getResponse().getMsg());
        else ps.println(result.getValue());
        updateHistory(alias);
    }

    private void updateHistory(String command) {
        history.add(command);
    }

    public CommandResult executeOnServer(UDPClient udp, Command command) {
        return udp.send(command.executable, command.args);
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

    public static class ExitCommand extends Command {
        public ExitCommand() {
            super("exit", true, "exit : завершить программу (без сохранения в файл)",
                    args -> {
                        instance.isActive = false;
                        return new CommandResult("Выход из программы...", DefaultResponse.OK);
                    });
        }

        @Override
        public boolean validate(String[] args) {
            return true;
        }
    }

    public static class HistoryCommand extends Command {
        public HistoryCommand() {
            super("history", true, "history [count] : вывести последние count введенных команд, по умолчанию count равен 6",
                    args -> {
                        int lines = args.length > 0 ? (int) args[0] : 6;
                        int start = lines < instance.history.size() ? instance.history.size() - lines : 0;
                        StringBuilder result = new StringBuilder("История последних команд:\n");
                        for (int i = start; i < instance.history.size(); i++)
                            result.append(instance.history.get(i)).append("\n");
                        return new CommandResult(result.toString(), DefaultResponse.OK);
                    });
        }

        @Override
        public boolean validate(String[] args) {
            Integer value = 6;
            this.args = new Object[]{ value };
            if (args.length > 0) {
                value = UtilFunctions.intOrNull(args[0]);
                if (value != null) {
                    this.args = new Object[]{ value };
                    return true;
                } else return false;
            }
            return true;
        }
    }

    public static class ExecuteScriptCommand extends Command {
        public ExecuteScriptCommand() {
            super("execute_script", true, "execute_script {file_name} : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.",
                    args -> {
                        String fileName = (String) args[0];
                        File file = new File(fileName);
                        if (!file.exists() || file.isDirectory())
                            return new CommandResult(null, DefaultResponse.FILE_NOT_FOUND);

                        Reader streamReader;
                        try {
                            streamReader = new InputStreamReader(new FileInputStream(file));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            return new CommandResult(null, DefaultResponse.UNKNOWN);
                        }
                        instance.addNewInput(streamReader, fileName);
                        return new CommandResult(null, DefaultResponse.OK);
                    });
        }

        @Override
        public boolean validate(String[] args) {
            if (args.length < 1) {
                System.err.println("Недостаточно данных");
                return false;
            }
            this.args = args;
            return true;
        }
    }

    public static class ModeCommand extends Command {
        public ModeCommand() {
            super("mode", true, "mode [sw] : выводит режим работы программы, если написать sw, меняет режим с серверного на клиентский или наоборот",
                    args -> {
                        String result;
                        if (args.length == 0) {
                            result = "Программа работает в режиме клиент " + (instance.clientMode ? "" : "+ сервер");
                        } else {
                            instance.clientMode = !instance.clientMode;
                            if (instance.clientMode) {
                                result = "Режим работы сменен на клиент";
                                instance.udp.disconnect();
                            } else {
                                result = "Режим работы сменен на клиент + сервер";
                                instance.udp.connect();
                            }
                        }
                        return new CommandResult(result, DefaultResponse.OK);
                    });
        }

        @Override
        public boolean validate(String[] args) {
            if (args.length > 0 && args[0].equals("sw")) this.args = args;
            else this.args = new Object[]{};
            return true;
        }
    }
}