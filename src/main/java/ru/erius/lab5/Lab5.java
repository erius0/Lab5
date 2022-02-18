package ru.erius.lab5;

import ru.erius.lab5.collection.Database;
import ru.erius.lab5.collection.PeopleDatabase;
import ru.erius.lab5.commandline.CommandLine;
import ru.erius.lab5.commandline.PeopleDatabaseCommands;

public class Lab5 {

    public static void main(String[] args) {
        CommandLine cmd = CommandLine.getInstance();

        PeopleDatabaseCommands.registerDatabaseCommands();
        PeopleDatabase peopleDatabase = new PeopleDatabase();
        try {
            peopleDatabase.load();
        } catch (Database.DatabaseLoadFailedException e) {
            e.printStackTrace();
        }
        PeopleDatabaseCommands.setPeopleDatabase(peopleDatabase);

        cmd.start();
    }
}
