package client.commandline;

import client.commandline.pdcommands.*;
import client.net.UDPClient;
import common.commandline.Command;
import common.commandline.CommandLineHandler;
import common.commandline.CommandRegistry;
import common.commandline.response.CommandResult;
import common.commandline.response.DefaultResponse;
import common.commandline.response.Response;
import common.net.ConnectionProperties;
import common.util.UtilFunctions;

import java.io.*;
import java.util.Locale;

public final class CommandLineHandlerClient extends CommandLineHandler {

    private final UDPClient udp = new UDPClient(ConnectionProperties.getHostname(), ConnectionProperties.getPort());

    private CommandLineHandlerClient() {
        super();
        CommandRegistry.registerCommands(new ConnectionCommand(), new InfoCommand(), new ShowCommand(), new AddCommand(), new AddIfMaxCommand(),
                new AddIfMinCommand(), new ClearCommand(), new FilterContainsNameCommand(), new PrintFieldDescendingLocationCommand(),
                new RemoveByIdCommand(), new SumOfHeightCommand(), new UpdateCommand());
    }

    public static CommandLineHandler getClientCommandLine() {
        instance = new CommandLineHandlerClient();
        return instance;
    }

    protected void executeCommand(String alias, String[] args) {
        Command command = CommandRegistry.getCommand(alias);
        if (command == null) {
            System.err.println("Неизвестная команда " + alias + ", напишите help для отображения всех существующих команд");
            return;
        }
        boolean argsValid = command.validate(args);
        if (!argsValid) return;
        CommandResult result = command.isClientOnly() ? command.executeOnClient() : executeOnServer(udp, command);
        PrintStream ps = result.getResponse() == DefaultResponse.OK ? System.out : System.err;
        ps.println(result.getValue());
        updateHistory(alias);
    }

    public CommandResult executeOnServer(UDPClient udp, Command command) {
        return udp.send(command.getExecutable(), command.getArgs());
    }

    public class ConnectionCommand extends Command {
        public ConnectionCommand() {
            super("con", true, "con [host|port] [value] : выводит информацию о сохраненных данных соединения с сервером, можно менять адрес или порт написав host или port и после них соответствующее значение");
            this.executable = args -> {
                String result;
                if (args.length == 0) {
                    result = String.format("Информация о соединении:\n\tАдрес - %s\n\tПорт - %d",
                            ConnectionProperties.getHostname(), ConnectionProperties.getPort());
                    return new CommandResult(result, DefaultResponse.OK);
                } else {
                    String change = (String) args[0];
                    if (change.equals("host")) {
                        String host = (String) args[1];
                        ConnectionProperties.setHostname(host);
                        udp.setHostname(host);
                        udp.disconnect();
                        udp.connect();
                        Response response = DefaultResponse.OK;
                        return new CommandResult(response.getMsg(), response);
                    } else {
                        int port = (Integer) args[1];
                        ConnectionProperties.setPort(port);
                        udp.setPort(port);
                        udp.disconnect();
                        udp.connect();
                        Response response = DefaultResponse.OK;
                        return new CommandResult(response.getMsg(), response);
                    }
                }
            };
        }

        @Override
        public boolean validate(String[] args) {
            if (args.length == 0){
                this.args = new Object[]{};
                return true;
            }
            if (args.length < 2) {
                System.err.println("Недостаточно аргументов");
                return false;
            }
            String change = args[0].toLowerCase(Locale.ROOT);
            if (change.equals("host")) {
                this.args = new Object[]{ change, args[1] };
                return true;
            } else if (change.equals("port")) {
                Integer port = UtilFunctions.intOrNull(args[1]);
                if (port == null || port < 0 || port > 65535) {
                    System.err.println("Порт должен быть целым числом от 0 до 65535");
                    return false;
                }
                this.args = new Object[]{ change, port };
                return true;
            }
            System.err.println("Неизвестное свойство " + args[0]);
            return false;
        }
    }
}
