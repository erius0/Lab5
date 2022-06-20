package common.commandline.pdcommands;

import common.commandline.Command;
import common.util.UtilFunctions;

public abstract class PeopleDatabaseCommand extends Command {

    public PeopleDatabaseCommand(String alias, boolean clientOnly, String description) {
        super(alias, clientOnly, description);
    }

    public static boolean validateId(String[] args) {
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
