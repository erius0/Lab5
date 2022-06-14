package server.sql;

import common.commandline.Executable;
import common.commandline.Executables;

import java.util.HashMap;
import java.util.Map;

public enum SQLQueries {
    INSERT_PERSON("SELECT insert_person(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", (statement, data) -> {
        statement.setString(1, data[1]);
    }),
    UPDATE_PERSON(""),
    INSERT_USER("");

    private static final Map<Executable, String> EXECUTABLE_SQL_QUERIES_MAP = new HashMap<>();

    static {
        EXECUTABLE_SQL_QUERIES_MAP.put(Executables.ADD.executable, INSERT_PERSON.query);
        EXECUTABLE_SQL_QUERIES_MAP.put(Executables.UPDATE.executable, UPDATE_PERSON.query);
    }

    public final String query;
    public final QuerySetup setup;

    SQLQueries(String query, QuerySetup setup) {
        this.query = query;
        this.setup = setup;
    }

    public static String getQueryByExecutable(Executable executable) {
        return EXECUTABLE_SQL_QUERIES_MAP.get(executable);
    }
}
