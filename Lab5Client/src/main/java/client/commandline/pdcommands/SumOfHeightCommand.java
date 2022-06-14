package client.commandline.pdcommands;

import common.commandline.Executables;
import common.commandline.pdcommands.PeopleDatabaseCommand;

public class SumOfHeightCommand extends PeopleDatabaseCommand {
    public SumOfHeightCommand() {
        super("sum_of_height", false, "sum_of_height : вывести сумму значений поля height для всех элементов коллекции",
                Executables.SUM_OF_HEIGHT.executable);
    }

    @Override
    public boolean validate(String[] args) {
        this.args = new Object[]{ null };
        return true;
    }
}
