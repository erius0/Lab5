package client.commandline.pdcommands;

import common.commandline.Executables;

public class InfoCommand extends PeopleDatabaseCommand {
    public InfoCommand() {
        super("info", false, "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)",
                Executables.INFO.getExecutable());
    }

    @Override
    public boolean validate(String[] args) {
        this.args = new Object[]{ null };
        return true;
    }
}
