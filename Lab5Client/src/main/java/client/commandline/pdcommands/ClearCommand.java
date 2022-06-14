package client.commandline.pdcommands;

import common.commandline.Executables;
import common.commandline.pdcommands.PeopleDatabaseCommand;

public class ClearCommand extends PeopleDatabaseCommand {
    public ClearCommand() {
        super("clear", false, "clear : очистить коллекцию",
                Executables.CLEAR.executable);
    }

    @Override
    public boolean validate(String[] args) {
        this.args = new Object[]{ null };
        return true;
    }
}