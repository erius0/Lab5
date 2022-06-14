package client.commandline.pdcommands;

import common.commandline.Executables;
import common.commandline.pdcommands.PeopleDatabaseCommand;
import common.util.UtilFunctions;

public class RemoveByIdCommand extends PeopleDatabaseCommand {
    public RemoveByIdCommand() {
        super("remove_by_id", false, "remove_by_id {id} : удалить элемент из коллекции по его {id}",
                Executables.REMOVE_BY_ID.executable);
    }

    @Override
    public boolean validate(String[] args) {
        if (PeopleDatabaseCommand.validateIdCommand(args)) {
            Long id = UtilFunctions.longOrNull(args[0]);
            this.args = new Object[]{ null, id };
            return true;
        }
        return false;
    }
}
