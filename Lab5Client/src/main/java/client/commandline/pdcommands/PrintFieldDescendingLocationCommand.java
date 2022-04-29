package client.commandline.pdcommands;

import common.commandline.Executables;

public class PrintFieldDescendingLocationCommand extends PeopleDatabaseCommand {
    public PrintFieldDescendingLocationCommand() {
        super("print_field_descending_location", false, "print_field_descending_location : вывести значения поля location всех элементов в порядке убывания",
                Executables.PRINT_FIELD_DESCENDING_LOCATION.getExecutable());
    }

    @Override
    public boolean validate(String[] args) {
        this.args = new Object[]{ null };
        return true;
    }
}
