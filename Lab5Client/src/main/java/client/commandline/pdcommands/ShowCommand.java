package client.commandline.pdcommands;

import common.commandline.Executables;
import common.commandline.pdcommands.PeopleDatabaseCommand;

public class ShowCommand extends PeopleDatabaseCommand {
    public ShowCommand() {
        super("show", false, "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении",
                Executables.SHOW.getExecutable());
    }

    @Override
    public boolean validate(String[] args) {
        this.args = new Object[]{ null };
        return true;
    }
}
