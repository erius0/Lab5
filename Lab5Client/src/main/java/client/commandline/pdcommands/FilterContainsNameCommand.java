package client.commandline.pdcommands;

import common.commandline.Executables;

public class FilterContainsNameCommand extends PeopleDatabaseCommand {
    public FilterContainsNameCommand() {
        super("filter_contains_name", false, "filter_contains_name {name} : вывести элементы, значение поля name которых содержит заданную подстроку",
                Executables.FILTER_CONTAINS_NAME.getExecutable());
    }

    @Override
    public boolean validate(String[] args) {
        if (args.length < 1) {
            System.err.println("Недостаточно данных");
            return false;
        }
        this.args = new Object[]{ null, args[0] };
        return true;
    }
}
