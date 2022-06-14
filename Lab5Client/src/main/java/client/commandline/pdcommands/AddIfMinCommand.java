package client.commandline.pdcommands;

import common.commandline.Executables;
import common.commandline.pdcommands.PeopleDatabaseCommand;
import common.commandline.pdcommands.PeopleDatabaseCommands;

public class AddIfMinCommand extends PeopleDatabaseCommand {
    public AddIfMinCommand() {
        super("add_if_min", false, "add_if_min <Person> : добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции",
                Executables.ADD_IF_MIN.executable);
    }

    @Override
    public boolean validate(String[] args) {
        this.args = new Object[]{ null, PeopleDatabaseCommands.createPerson() };
        return true;
    }
}
