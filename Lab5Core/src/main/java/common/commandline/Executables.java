package common.commandline;

import common.collection.PeopleCollection;
import common.commandline.response.CommandResult;
import common.commandline.response.DefaultResponse;
import common.commandline.response.PeopleDatabaseResponse;
import common.commandline.response.Response;
import common.data.Person;

import java.util.Collections;
import java.util.Optional;

public enum Executables {
    ADD(args -> {
        PeopleCollection peopleCollection = (PeopleCollection) args[0];
        Person person = (Person) args[1];
        boolean success = peopleCollection.getCollection().add(person);
        Response response = PeopleDatabaseResponse.FAILED_TO_ADD;
        if (success) response = DefaultResponse.OK;
        return new CommandResult(response.getMsg(), response);
    }),

    ADD_IF_MAX(args -> {
        PeopleCollection peopleCollection = (PeopleCollection) args[0];
        Person person = (Person) args[1];
        Person last = peopleCollection.getCollection().last();
        if (person.compareTo(last) > 0) return ADD.executable.execute(args);
        Response response = PeopleDatabaseResponse.FAILED_TO_ADD;
        return new CommandResult(response.getMsg(), response);
    }),

    ADD_IF_MIN(args -> {
        PeopleCollection peopleCollection = (PeopleCollection) args[0];
        Person person = (Person) args[1];
        Person first = peopleCollection.getCollection().first();
        if (person.compareTo(first) < 0) return ADD.executable.execute(args);
        Response response = PeopleDatabaseResponse.FAILED_TO_ADD;
        return new CommandResult(response.getMsg(), response);
    }),

    CLEAR(args -> {
        PeopleCollection peopleCollection = (PeopleCollection) args[0];
        peopleCollection.getCollection().clear();
        Response response = DefaultResponse.OK;
        return new CommandResult(response.getMsg(), response);
    }),

    FILTER_CONTAINS_NAME(args -> {
        PeopleCollection peopleCollection = (PeopleCollection) args[0];
        String name = (String) args[1];
        StringBuilder result = new StringBuilder("Список людей, в имени которых содержится " + name + ":\n");
        peopleCollection.getCollection()
                .stream()
                .filter(p -> p.getName().contains(name))
                .forEach(p -> result.append(p.formatted()).append("\n"));
        return new CommandResult(result.toString(), DefaultResponse.OK);
    }),

    INFO(args -> {
        PeopleCollection peopleCollection = (PeopleCollection) args[0];
        return new CommandResult(peopleCollection.info(), DefaultResponse.OK);
    }),

    PRINT_FIELD_DESCENDING_LOCATION(args -> {
        PeopleCollection peopleCollection = (PeopleCollection) args[0];
        StringBuilder result = new StringBuilder("Список локаций в порядке убывания:\n");
        peopleCollection.getCollection()
                .stream()
                .map(Person::getLocation)
                .sorted(Collections.reverseOrder())
                .forEach(loc -> result.append(loc).append("\n"));
        return new CommandResult(result.toString(), DefaultResponse.OK);
    }),

    REMOVE_BY_ID(args -> {
        PeopleCollection peopleCollection = (PeopleCollection) args[0];
        long id = (long) args[1];
        boolean success = peopleCollection.getCollection().removeIf(p -> p.getId().equals(id));
        Response response = success ? DefaultResponse.OK : PeopleDatabaseResponse.ELEMENT_NOT_FOUND;
        return new CommandResult(response.getMsg(), response);
    }),

    SHOW(args -> {
        PeopleCollection peopleCollection = (PeopleCollection) args[0];
        StringBuilder result = new StringBuilder("Элементы коллекции:\n");
        peopleCollection.getCollection().forEach(p -> result.append(p.formatted()));
        return new CommandResult(result.toString(), DefaultResponse.OK);
    }),

    SUM_OF_HEIGHT(args -> {
        PeopleCollection peopleCollection = (PeopleCollection) args[0];
        String sum = "Сумма ростов всех людей в коллекции - " + peopleCollection.getCollection()
                .stream()
                .mapToInt(p -> p.getHeight() == null ? 0 : p.getHeight())
                .sum();
        return new CommandResult(sum, DefaultResponse.OK);
    }),

    UPDATE(args -> {
        PeopleCollection peopleCollection = (PeopleCollection) args[0];
        long id = (long) args[1];
        Person person = (Person) args[2];
        Optional<Person> optionalPerson = peopleCollection.getCollection()
                .stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
        Response response = PeopleDatabaseResponse.ELEMENT_NOT_FOUND;
        if (!optionalPerson.isPresent()) return new CommandResult(response.getMsg(), response);
        Person oldPerson = optionalPerson.get();
        CommandResult result = REMOVE_BY_ID.executable.execute(new Object[]{peopleCollection, id });
        if (result.getResponse() != DefaultResponse.OK) return result;
        oldPerson.update(person);
        return ADD.executable.execute(new Object[]{peopleCollection, oldPerson });
    });

    public final Executable executable;

    Executables(Executable executable) {
        this.executable = executable;
    }
}
