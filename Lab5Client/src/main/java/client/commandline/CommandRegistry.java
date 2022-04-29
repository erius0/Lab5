package client.commandline;

import common.commandline.response.CommandResult;
import common.commandline.response.DefaultResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс регистра команд, используемый для добавления, переопределения или удаления команд
 */
public final class CommandRegistry {

    private static final Map<String, Command> COMMANDS = new HashMap<>();

    private CommandRegistry() {
    }

    static {
        registerCommand(new HelpCommand());
    }

    public static void registerCommand(Command command) {
        if (COMMANDS.containsKey(command.getAlias()))
            throw new CommandAlreadyExistsException("Команда %s уже существует, " +
                    "используйте метод reassignCommand() для переопределения существующей команды", command.getAlias());
        COMMANDS.put(command.getAlias(), command);
    }

    public static void registerCommands(Command... commands) {
        for (Command command : commands) registerCommand(command);
    }

    public static void reassignCommand(Command command) {
        if (!COMMANDS.containsKey(command.getAlias()))
            throw new CommandNotFoundException("Не удалось переопределить несуществующую команду %s", command.getAlias());
        COMMANDS.put(command.getAlias(), command);
    }

    public static void unregisterCommand(String alias) {
        if (!COMMANDS.containsKey(alias))
            throw new CommandNotFoundException("Не удалось удалить несуществующую команду %s", alias);
        COMMANDS.remove(alias);
    }

    public static Command getCommand(String alias) {
        return COMMANDS.get(alias);
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

    public static class HelpCommand extends Command {
        public HelpCommand() {
            super("help", true, "help : вывести справку по доступным командам",
                    args -> {
                        StringBuilder result = new StringBuilder(LongStrings.LINE.getValue() + "\n\n");
                        for (Command c : COMMANDS.values()) {
                            String desc = c.getDescription();
                            if (desc != null) result.append(desc).append("\n\n");
                        }
                        result.append(LongStrings.LINE.getValue());
                        return new CommandResult(result.toString(), DefaultResponse.OK);
                    });
        }

        @Override
        public boolean validate(String[] args) {
            return true;
        }
    }
}
