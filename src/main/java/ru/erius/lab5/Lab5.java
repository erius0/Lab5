package ru.erius.lab5;

import ru.erius.lab5.collection.Database;
import ru.erius.lab5.collection.PeopleDatabase;
import ru.erius.lab5.commandline.CommandLineHandler;
import ru.erius.lab5.commandline.PeopleDatabaseCommands;

public class Lab5 {

    public static void main(String[] args) {
        CommandLineHandler cmd = CommandLineHandler.getInstance();

        PeopleDatabaseCommands.registerDatabaseCommands();
        PeopleDatabase peopleDatabase = new PeopleDatabase();
        try {
            peopleDatabase.load();
        } catch (Database.DatabaseLoadFailedException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        PeopleDatabaseCommands.setPeopleDatabase(peopleDatabase);

        cmd.start();
    }
}
