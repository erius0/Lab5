package client.commandline.pdcommands;

import common.commandline.Executables;
import common.commandline.pdcommands.PeopleDatabaseCommand;
import common.commandline.pdcommands.PeopleDatabaseCommands;

public class AddIfMaxCommand extends PeopleDatabaseCommand {
    public AddIfMaxCommand() {
        super("add_if_max", false, "add_if_max <Person> : добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции",
                Executables.ADD_IF_MAX.executable);
    }

    @Override
    public boolean validate(String[] args) {
        this.args = new Object[]{ null, PeopleDatabaseCommands.createPerson() };
        return true;
    }
}
