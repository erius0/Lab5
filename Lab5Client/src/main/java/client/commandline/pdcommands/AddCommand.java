package client.commandline.pdcommands;

import common.commandline.Executables;
import common.commandline.pdcommands.PeopleDatabaseCommand;
import common.commandline.pdcommands.PeopleDatabaseCommands;

public class AddCommand extends PeopleDatabaseCommand {
    public AddCommand() {
        super("add", false, "add <Person> : добавить новый элемент в коллекцию",
                Executables.ADD.executable);
    }

    @Override
    public boolean validate(String[] args) {
        this.args = new Object[]{ null, PeopleDatabaseCommands.createPerson() };
        return true;
    }
}
