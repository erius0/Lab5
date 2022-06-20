package common.commandline.commands;

import common.commandline.Command;
import common.commandline.PlaceHolder;
import common.commandline.response.CommandResult;
import common.commandline.response.Response;
import common.commandline.response.SqlResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LogInCommand extends Command {

    public LogInCommand() {
        super("login", false, "login");
    }

    @Override
    public Object[] validate(String[] args) {
        return new Object[]{args[0], args[1], PlaceHolder.of(Connection.class)};
    }

    @Override
    public CommandResult execute(Object[] args) {
        String login = (String) args[0];
        String password = (String) args[1];
        Connection connection = (Connection) args[2];
        Response response;
        try {
            PreparedStatement statement = connection.prepareStatement(
                        "SELECT COUNT(1) FROM users " +
                            "WHERE login = ? AND password = ?;"
            );
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet result = statement.executeQuery();
            result.next();
            boolean authorize = result.getInt(1) > 0;
            response = authorize ? SqlResponse.OK : SqlResponse.WRONG_CREDENTIALS;
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            response = SqlResponse.UNKNOWN;
        }
        return new CommandResult(response.getMsg(), response);
    }
}
