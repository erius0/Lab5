package server.commandline;

import common.commandline.Command;
import common.commandline.CommandLineHandler;
import common.commandline.CommandRegistry;
import common.commandline.response.CommandResult;
import common.commandline.response.DefaultResponse;

import java.io.PrintStream;

public class CommandLineHandlerServer extends CommandLineHandler {

    private CommandLineHandlerServer() {
        super();
    }

    public static CommandLineHandler getServerCommandLine() {
        if (instance == null) instance = new CommandLineHandlerServer();
        return instance;
    }

    protected void executeCommand(String alias, String[] args) {
        Command command = CommandRegistry.getCommand(alias);
        if (command == null) {
            System.err.println("Неизвестная команда " + alias + ", напишите help для отображения всех существующих команд");
            return;
        }
        Object[] objArgs = command.validate(args);
        if (objArgs == null) return;
        CommandResult result = command.execute(objArgs);
        PrintStream ps = result.getResponse() == DefaultResponse.OK ? System.out : System.err;
        ps.println(result.getValue());
        updateHistory(alias);
    }
}
