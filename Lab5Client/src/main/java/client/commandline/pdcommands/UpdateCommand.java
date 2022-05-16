package client.commandline.pdcommands;

import common.commandline.Executables;
import common.commandline.pdcommands.PeopleDatabaseCommand;
import common.commandline.pdcommands.PeopleDatabaseCommands;
import common.util.UtilFunctions;

public class UpdateCommand extends PeopleDatabaseCommand {
    public UpdateCommand() {
        super("update", false, "update {id} <Person> : обновить значение элемента коллекции, {id} которого равен заданному",
                Executables.UPDATE.getExecutable());
    }

    @Override
    public boolean validate(String[] args) {
        if (PeopleDatabaseCommand.validateIdCommand(args)) {
            Long id = UtilFunctions.longOrNull(args[0]);
            this.args = new Object[]{ null, id, PeopleDatabaseCommands.createPerson() };
            return true;
        }
        return false;
    }
}
