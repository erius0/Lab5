package client.commandline.pdcommands;

import common.commandline.Executables;
import common.commandline.pdcommands.PeopleDatabaseCommand;

public class InfoCommand extends PeopleDatabaseCommand {
    public InfoCommand() {
        super("info", false, "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)",
                Executables.INFO.executable);
    }

    @Override
    public boolean validate(String[] args) {
        this.args = new Object[]{ null };
        return true;
    }
}
