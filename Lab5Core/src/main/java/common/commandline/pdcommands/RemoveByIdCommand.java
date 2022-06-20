package common.commandline.pdcommands;

import common.collection.PeopleCollection;
import common.commandline.CommandLineHandler;
import common.commandline.PlaceHolder;
import common.commandline.response.*;
import common.util.UtilFunctions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RemoveByIdCommand extends PeopleDatabaseCommand {
    public RemoveByIdCommand() {
        super("remove_by_id", false, "remove_by_id {id} : удалить элемент из коллекции по его {id}");
    }

    @Override
    public Object[] validate(String[] args) {
        if (PeopleDatabaseCommand.validateId(args)) {
            Long id = UtilFunctions.longOrNull(args[0]);
            return new Object[]{id, CommandLineHandler.getUser(), PlaceHolder.of(PeopleCollection.class), PlaceHolder.of(Connection.class)};
        }
        return null;
    }

    @Override
    public CommandResult execute(Object[] args) {
        long id = (long) args[0];
        String owner = (String) args[1];
        PeopleCollection peopleCollection = (PeopleCollection) args[2];
        Connection connection = (Connection) args[3];
        Response response;
        try {
            PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM people USING users " +
                            "WHERE users.id = owner_id AND people.id = ? AND (login = ? OR is_admin);"
            );
            statement.setLong(1, id);
            statement.setString(2, owner);
            int result = statement.executeUpdate();
            response = result > 0 ? SqlResponse.OK : SqlResponse.NOT_FOUND;
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            response = SqlResponse.UNKNOWN;
            return new CommandResult(response.getMsg(), response);
        }
        if (response == SqlResponse.OK)
            peopleCollection.getCollection().removeIf(p -> p.getId().equals(id));
        String msg = response == SqlResponse.NOT_FOUND ?
                "Человека с таким id не существует, либо вы не имеете права на его модификацию" : response.getMsg();
        return new CommandResult(msg, response);
    }
}
