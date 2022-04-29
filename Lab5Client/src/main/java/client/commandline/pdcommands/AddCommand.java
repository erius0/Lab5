package client.commandline.pdcommands;

import common.commandline.Executables;

public class AddCommand extends PeopleDatabaseCommand {
    public AddCommand() {
        super("add", false, "add <Person> : добавить новый элемент в коллекцию",
                Executables.ADD.getExecutable());
    }

    @Override
    public boolean validate(String[] args) {
        this.args = new Object[]{ null, PeopleDatabaseCommands.createPerson() };
        return true;
    }
}
