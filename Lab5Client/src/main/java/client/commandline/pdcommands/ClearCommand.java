package client.commandline.pdcommands;

import common.commandline.Executables;

public class ClearCommand extends PeopleDatabaseCommand {
    public ClearCommand() {
        super("clear", false, "clear : очистить коллекцию",
                Executables.CLEAR.getExecutable());
    }

    @Override
    public boolean validate(String[] args) {
        this.args = new Object[]{ null };
        return true;
    }
}