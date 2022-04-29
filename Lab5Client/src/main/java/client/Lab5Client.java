package client;

import client.commandline.CommandLineHandler;
import client.commandline.pdcommands.PeopleDatabaseCommands;
import common.collection.Database;
import common.collection.PeopleDatabase;

import java.util.logging.Logger;

public class Lab5Client {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("Lab5Client");
        CommandLineHandler cmd = CommandLineHandler.getInstance();

        PeopleDatabaseCommands.registerDatabaseCommands();
        PeopleDatabase peopleDatabase = new PeopleDatabase(logger);
        try {
            peopleDatabase.load();
        } catch (Database.DatabaseLoadFailedException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        PeopleDatabaseCommands.peopleDatabase = peopleDatabase;

        cmd.start();
    }
}
