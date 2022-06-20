package common.commandline.pdcommands;

import common.collection.PeopleCollection;
import common.commandline.PlaceHolder;
import common.commandline.response.CommandResult;
import common.commandline.response.DefaultResponse;

public class InfoCommand extends PeopleDatabaseCommand {
    public InfoCommand() {
        super("info", false, "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
    }

    @Override
    public Object[] validate(String[] args) {
        return new Object[]{PlaceHolder.of(PeopleCollection.class)};
    }

    @Override
    public CommandResult execute(Object[] args) {
        PeopleCollection peopleCollection = (PeopleCollection) args[0];
        return new CommandResult(peopleCollection.info(), DefaultResponse.OK);
    }
}
