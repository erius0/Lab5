package ru.erius.lab5.commandline;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Класс регистра команд, используемый для добавления, переопределения или удаления команд
 */
public final class CommandRegistry {

    private static final Map<String, DescriptiveCommand> COMMANDS = new HashMap<>();

    private CommandRegistry() {
    }

    static {
        registerCommand("help", args -> help(),
                "help : вывести справку по доступным командам");
    }

    public static void registerCommand(String alias, Command command) {
        registerCommand(alias, command, null);
    }

    public static void registerCommand(String alias, Command command, String desc) {
        if (COMMANDS.containsKey(alias))
            throw new CommandAlreadyExistsException("Команда %s уже существует, " +
                    "используйте метод reassignCommand() для переопределения существующей команды", alias);
        COMMANDS.put(alias, new DescriptiveCommand(command, desc));
    }

    public static void reassignCommand(String alias, Command command, String desc) {
        if (!COMMANDS.containsKey(alias))
            throw new CommandNotFoundException("Не удалось переопределить несуществующую команду %s", alias);
        COMMANDS.put(alias, new DescriptiveCommand(command, desc));
    }

    public static void unregisterCommand(String alias) {
        if (!COMMANDS.containsKey(alias))
            throw new CommandNotFoundException("Не удалось удалить несуществующую команду %s", alias);
        COMMANDS.remove(alias);
    }

    public static Command getCommand(String alias) {
        DescriptiveCommand command = COMMANDS.get(alias);
        return command == null ? null : command.getCommand() ;
    }

    public static void help() {
        System.out.println(LongStrings.LINE.getValue() + "\n");
        List<DescriptiveCommand> commands = COMMANDS.values().stream()
                .sorted(Comparator.comparing(DescriptiveCommand::getDesc))
                .collect(Collectors.toList());
        for (DescriptiveCommand dc : commands) {
            String desc = dc.getDesc();
            if (desc != null) System.out.println(desc + "\n");
        }
        System.out.println(LongStrings.LINE.getValue());
    }

    public static class CommandNotFoundException extends RuntimeException {

        private CommandNotFoundException(String message) {
            super(message);
        }

        private CommandNotFoundException(String message, String alias) {
            super(String.format(message, alias));
        }
    }

    public static class CommandAlreadyExistsException extends RuntimeException {

        private CommandAlreadyExistsException(String message) {
            super(message);
        }

        private CommandAlreadyExistsException(String message, String alias) {
            super(String.format(message, alias));
        }
    }
}
