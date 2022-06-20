package common.commandline.pdcommands;

import common.collection.PeopleCollection;
import common.commandline.CommandLineHandler;
import common.commandline.PlaceHolder;
import common.commandline.response.CommandResult;
import common.commandline.response.Response;
import common.commandline.response.SqlResponse;
import common.data.Person;
import common.util.UtilFunctions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UpdateCommand extends PeopleDatabaseCommand {
    public UpdateCommand() {
        super("update", false, "update {id} <Person> : обновить значение элемента коллекции, {id} которого равен заданному");
    }

    @Override
    public Object[] validate(String[] args) {
        if (PeopleDatabaseCommand.validateId(args)) {
            Long id = UtilFunctions.longOrNull(args[0]);
            return new Object[]{id, CommandLineHandler.getUser(), PeopleDatabaseCommands.createPerson(), PlaceHolder.of(PeopleCollection.class), PlaceHolder.of(Connection.class)};
        }
        return null;
    }

    @Override
    public CommandResult execute(Object[] args) {
        long id = (long) args[0];
        String owner = (String) args[1];
        Person person = (Person) args[2];
        PeopleCollection peopleCollection = (PeopleCollection) args[3];
        Connection connection = (Connection) args[4];
        Response response;
        boolean success;
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT update_person(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
            );
            statement.setLong(1, id);
            statement.setString(2, person.getName());
            statement.setFloat(3, person.getCoordinates().getX());
            statement.setFloat(4, person.getCoordinates().getY());
            statement.setInt(5, person.getHeight());
            statement.setString(6, person.getPassportID());
            statement.setString(7, UtilFunctions.allLowerFirstCapital(person.getEyeColor().name()));
            statement.setString(8, UtilFunctions.allLowerFirstCapital(person.getNationality().name()));
            statement.setString(9, person.getLocation().getName());
            statement.setDouble(10, person.getLocation().getX());
            statement.setFloat(11, person.getLocation().getY());
            statement.setLong(12, person.getLocation().getZ());
            statement.setString(13, owner);
            ResultSet result = statement.executeQuery();
            result.next();
            success = result.getBoolean(1);
            response = SqlResponse.OK;
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            response = SqlResponse.UNKNOWN;
            return new CommandResult(response.getMsg(), response);
        }
        if (!success) {
            response = SqlResponse.NOT_FOUND;
            String msg = "Элемент не найден, либо у вас недостаточно прав на его изменение";
            return new CommandResult(msg, response);
        }
        Optional<Person> optionalPerson = peopleCollection.getCollection()
                .stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
        Person oldPerson = optionalPerson.get();
        oldPerson.update(person);
        return new CommandResult(response.getMsg(), response);
    }
}
