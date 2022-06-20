package common.commandline.pdcommands;

import common.collection.PeopleCollection;
import common.commandline.PlaceHolder;
import common.commandline.response.CommandResult;
import common.commandline.response.DefaultResponse;
import common.data.Person;

import java.util.Collections;

public class PrintFieldDescendingLocationCommand extends PeopleDatabaseCommand {
    public PrintFieldDescendingLocationCommand() {
        super("print_field_descending_location", false, "print_field_descending_location : вывести значения поля location всех элементов в порядке убывания");
    }

    @Override
    public Object[] validate(String[] args) {
        return new Object[]{PlaceHolder.of(PeopleCollection.class)};
    }

    @Override
    public CommandResult execute(Object[] args) {
        PeopleCollection peopleCollection = (PeopleCollection) args[0];
        StringBuilder result = new StringBuilder("Список локаций в порядке убывания:\n");
        peopleCollection.getCollection()
                .stream()
                .map(Person::getLocation)
                .sorted(Collections.reverseOrder())
                .forEach(loc -> result.append(loc).append("\n"));
        return new CommandResult(result.toString(), DefaultResponse.OK);
    }
}
