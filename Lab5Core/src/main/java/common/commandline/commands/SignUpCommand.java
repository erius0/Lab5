package common.commandline.commands;

import common.commandline.Command;
import common.commandline.PlaceHolder;
import common.commandline.response.CommandResult;
import common.commandline.response.Response;
import common.commandline.response.SqlResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUpCommand extends Command {

    public SignUpCommand() {
        super("signup", false, "signup");
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
                    "INSERT INTO users (login, password) " +
                            "VALUES(?, ?);"
            );
            statement.setString(1, login);
            statement.setString(2, password);
            statement.executeUpdate();
            response = SqlResponse.OK;
            connection.commit();
        } catch (SQLException e) {
            String state = e.getSQLState();
            e.printStackTrace();
            if (state.equals("23505")) response = SqlResponse.LOGIN_EXISTS;
            else response = SqlResponse.UNKNOWN;
        }
        return new CommandResult(response.getMsg(), response);
    }
}
