package client.commandline.pdcommands;

import client.commandline.Command;
import common.commandline.Executable;
import common.commandline.response.CommandResult;
import common.util.UtilFunctions;

public abstract class PeopleDatabaseCommand extends Command {
    public PeopleDatabaseCommand(String alias, boolean clientOnly, String description, Executable executable) {
        super(alias, clientOnly, description, executable);
    }

    public CommandResult executeOnClient() {
        args[0] = PeopleDatabaseCommands.peopleDatabase;
        return super.executeOnClient();
    }

    public static boolean validateIdCommand(String[] args) {
        if (args.length < 1) {
            System.err.println("Недостаточно данных");
            return false;
        }
        Long id = UtilFunctions.longOrNull(args[0]);
        if (id == null) {
            System.err.println("{id} должен быть целым числом");
            return false;
        }
        return true;
    }
}
