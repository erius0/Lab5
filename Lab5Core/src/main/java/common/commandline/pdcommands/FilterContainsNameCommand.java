package common.commandline.pdcommands;

import common.collection.PeopleCollection;
import common.commandline.PlaceHolder;
import common.commandline.response.CommandResult;
import common.commandline.response.DefaultResponse;

public class FilterContainsNameCommand extends PeopleDatabaseCommand {
    public FilterContainsNameCommand() {
        super("filter_contains_name", false, "filter_contains_name {name} : вывести элементы, значение поля name которых содержит заданную подстроку");
    }

    @Override
    public Object[] validate(String[] args) {
        if (args.length < 1) {
            System.err.println("Недостаточно данных");
            return null;
        }
        return new Object[]{ args[0], PlaceHolder.of(PeopleCollection.class)};
    }

    @Override
    public CommandResult execute(Object[] args) {
        String name = (String) args[0];
        PeopleCollection peopleCollection = (PeopleCollection) args[1];
        StringBuilder result = new StringBuilder("Список людей, в имени которых содержится " + name + ":\n");
        peopleCollection.getCollection()
                .stream()
                .filter(p -> p.getName().contains(name))
                .forEach(p -> result.append(p.formatted()).append("\n"));
        return new CommandResult(result.toString(), DefaultResponse.OK);
    }
}
