package common.commandline.pdcommands;

import common.collection.PeopleCollection;
import common.commandline.PlaceHolder;
import common.commandline.response.CommandResult;
import common.commandline.response.DefaultResponse;

public class SumOfHeightCommand extends PeopleDatabaseCommand {
    public SumOfHeightCommand() {
        super("sum_of_height", false, "sum_of_height : вывести сумму значений поля height для всех элементов коллекции");
    }

    @Override
    public Object[] validate(String[] args) {
        return new Object[]{PlaceHolder.of(PeopleCollection.class)};
    }

    @Override
    public CommandResult execute(Object[] args) {
        PeopleCollection peopleCollection = (PeopleCollection) args[0];
        String sum = "Сумма ростов всех людей в коллекции - " + peopleCollection.getCollection()
                .stream()
                .mapToInt(p -> p.getHeight() == null ? 0 : p.getHeight())
                .sum();
        return new CommandResult(sum, DefaultResponse.OK);
    }
}
