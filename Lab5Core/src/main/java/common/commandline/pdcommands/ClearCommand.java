package common.commandline.pdcommands;

import common.collection.PeopleCollection;
import common.commandline.CommandLineHandler;
import common.commandline.PlaceHolder;
import common.commandline.response.CommandResult;
import common.commandline.response.DefaultResponse;
import common.commandline.response.Response;
import common.commandline.response.SqlResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClearCommand extends PeopleDatabaseCommand {
    public ClearCommand() {
        super("clear", false, "clear : очистить коллекцию");
    }

    @Override
    public Object[] validate(String[] args) {
        return new Object[]{CommandLineHandler.getUser(), PlaceHolder.of(PeopleCollection.class), PlaceHolder.of(Connection.class)};
    }

    @Override
    public CommandResult execute(Object[] args) {
        String owner = (String) args[0];
        PeopleCollection peopleCollection = (PeopleCollection) args[1];
        Connection connection = (Connection) args[2];
        try {
            PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM people " +
                            "USING users " +
                            "WHERE users.id = owner_id AND login = ?;"
            );
            statement.setString(1, owner);
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            Response response = SqlResponse.UNKNOWN;
            return new CommandResult(response.getMsg(), response);
        }
        peopleCollection.getCollection().removeIf(p -> p.getOwner().equals(owner));
        Response response = DefaultResponse.OK;
        return new CommandResult(response.getMsg(), response);
    }
}