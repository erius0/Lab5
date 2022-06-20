package common.commandline.pdcommands;

import common.collection.PeopleCollection;
import common.commandline.CommandLineHandler;
import common.commandline.PlaceHolder;
import common.commandline.response.CommandResult;
import common.commandline.response.DefaultResponse;
import common.commandline.response.Response;
import common.commandline.response.SqlResponse;
import common.data.Person;
import common.util.UtilFunctions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddCommand extends PeopleDatabaseCommand {
    public AddCommand() {
        super("add", false, "add <Person> : добавить новый элемент в коллекцию");
    }

    protected AddCommand(String alias, String description) {
        super(alias, false, description);
    }

    @Override
    public Object[] validate(String[] args) {
        return new Object[]{CommandLineHandler.getUser(), PeopleDatabaseCommands.createPerson(), PlaceHolder.of(PeopleCollection.class), PlaceHolder.of(Connection.class)};
    }

    @Override
    public CommandResult execute(Object[] args) {
        String owner = (String) args[0];
        Person person = (Person) args[1];
        PeopleCollection peopleCollection = (PeopleCollection) args[2];
        Connection connection = (Connection) args[3];
        long id;
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT insert_person(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
            );
            statement.setString(1, person.getName());
            statement.setFloat(2, person.getCoordinates().getX());
            statement.setFloat(3, person.getCoordinates().getY());
            statement.setInt(4, person.getHeight());
            statement.setString(5, person.getPassportID());
            statement.setString(6, UtilFunctions.allLowerFirstCapital(person.getEyeColor().name()));
            statement.setString(7, UtilFunctions.allLowerFirstCapital(person.getNationality().name()));
            statement.setString(8, person.getLocation().getName());
            statement.setDouble(9, person.getLocation().getX());
            statement.setFloat(10, person.getLocation().getY());
            statement.setLong(11, person.getLocation().getZ());
            statement.setString(12, owner);
            ResultSet result = statement.executeQuery();
            result.next();
            id = result.getLong(1);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            Response response = SqlResponse.UNKNOWN;
            return new CommandResult(response.getMsg(), response);
        }
        if (id <= 0) {
            Response response = SqlResponse.NOT_FOUND;
            return new CommandResult(response.getMsg(), response);
        }
        person.setId(id);
        peopleCollection.getCollection().add(person);
        Response response = DefaultResponse.OK;
        return new CommandResult(response.getMsg(), response);
    }
}
