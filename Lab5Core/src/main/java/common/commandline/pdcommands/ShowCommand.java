package common.commandline.pdcommands;

import common.collection.PeopleCollection;
import common.commandline.PlaceHolder;
import common.commandline.response.CommandResult;
import common.commandline.response.DefaultResponse;

public class ShowCommand extends PeopleDatabaseCommand {
    public ShowCommand() {
        super("show", false, "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
    }

    @Override
    public Object[] validate(String[] args) {
        return new Object[]{PlaceHolder.of(PeopleCollection.class)};
    }

    @Override
    public CommandResult execute(Object[] args) {
        PeopleCollection peopleCollection = (PeopleCollection) args[0];
        StringBuilder result = new StringBuilder("Элементы коллекции:\n");
        peopleCollection.getCollection().forEach(p -> result.append(p.formatted()));
        return new CommandResult(result.toString(), DefaultResponse.OK);
    }
}
