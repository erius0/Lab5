package server.sql;

import java.sql.PreparedStatement;

@FunctionalInterface
public interface QuerySetup {
    void setup(PreparedStatement statement, String[] data);
}
