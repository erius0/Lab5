package client.commandline;

import client.Lab5Client;
import common.commandline.Command;
import common.commandline.CommandLineHandler;
import common.commandline.CommandRegistry;
import common.commandline.pdcommands.*;
import common.commandline.response.CommandResult;
import common.commandline.response.DefaultResponse;

import java.io.*;

public final class CommandLineHandlerClient extends CommandLineHandler {

    private CommandLineHandlerClient() {
        super();
        CommandRegistry.registerCommands(new InfoCommand(), new ShowCommand(), new AddCommand(), new AddIfMaxCommand(),
                new AddIfMinCommand(), new ClearCommand(), new FilterContainsNameCommand(), new PrintFieldDescendingLocationCommand(),
                new RemoveByIdCommand(), new SumOfHeightCommand(), new UpdateCommand());
    }

    public static CommandLineHandler getClientCommandLine() {
        if (instance == null) instance = new CommandLineHandlerClient();
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
        CommandResult result = command.isClientOnly() ? command.execute(objArgs) : executeOnServer(command, objArgs);
        PrintStream ps = result.getResponse() == DefaultResponse.OK ? System.out : System.err;
        ps.println(result.getValue());
        updateHistory(alias);
    }

    public static CommandResult executeOnServer(Command command, Object[] args) {
        return Lab5Client.UDP.send(command, args);
    }
}