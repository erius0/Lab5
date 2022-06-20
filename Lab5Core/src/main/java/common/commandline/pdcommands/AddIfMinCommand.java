package common.commandline.pdcommands;

import common.collection.PeopleCollection;
import common.commandline.response.CommandResult;
import common.commandline.response.PeopleDatabaseResponse;
import common.commandline.response.Response;
import common.data.Person;

public class AddIfMinCommand extends PeopleDatabaseCommand {
    public AddIfMinCommand() {
        super("add_if_min", false, "add_if_min <Person> : добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции");
    }

    @Override
    public Object[] validate(String[] args) {
        return new AddCommand().validate(args);
    }

    @Override
    public CommandResult execute(Object[] args) {
        Person person = (Person) args[1];
        PeopleCollection peopleCollection = (PeopleCollection) args[2];
        Person first = peopleCollection.getCollection().last();
        if (person.compareTo(first) < 0) return new AddCommand().execute(args);
        Response response = PeopleDatabaseResponse.FAILED_TO_ADD;
        return new CommandResult(response.getMsg(), response);
    }
}
