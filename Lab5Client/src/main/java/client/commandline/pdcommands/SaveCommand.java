package client.commandline.pdcommands;

import common.commandline.Executables;

public class SaveCommand extends PeopleDatabaseCommand {
    public SaveCommand() {
        super("save", false, "save : сохранить коллекцию в файл",
                Executables.SAVE.getExecutable());
    }

    @Override
    public boolean validate(String[] args) {
        this.args = new Object[]{ null };
        return true;
    }
}
