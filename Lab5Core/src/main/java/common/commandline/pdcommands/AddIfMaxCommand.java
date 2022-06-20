package common.commandline.pdcommands;

import common.collection.PeopleCollection;
import common.commandline.response.CommandResult;
import common.commandline.response.PeopleDatabaseResponse;
import common.commandline.response.Response;
import common.data.Person;

public class AddIfMaxCommand extends PeopleDatabaseCommand {
    public AddIfMaxCommand() {
        super("add_if_max", false, "add_if_max <Person> : добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции");
    }

    @Override
    public Object[] validate(String[] args) {
        return new AddCommand().validate(args);
    }

    @Override
    public CommandResult execute(Object[] args) {
        Person person = (Person) args[1];
        PeopleCollection peopleCollection = (PeopleCollection) args[2];
        Person last = peopleCollection.getCollection().last();
        if (person.compareTo(last) > 0) return new AddCommand().execute(args);
        Response response = PeopleDatabaseResponse.FAILED_TO_ADD;
        return new CommandResult(response.getMsg(), response);
    }
}
