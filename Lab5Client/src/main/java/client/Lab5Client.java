package client;

import client.commandline.CommandLineHandler;
import client.commandline.pdcommands.PeopleDatabaseCommands;
import common.collection.Database;
import common.collection.PeopleDatabase;
import common.util.UtilFunctions;

import java.util.logging.Logger;

public class Lab5Client {

    public final static Logger LOGGER = UtilFunctions.getLogger(Lab5Client.class, "client");

    public static void main(String[] args) {
        CommandLineHandler cmd = CommandLineHandler.getInstance();

        PeopleDatabaseCommands.registerDatabaseCommands();
        PeopleDatabase peopleDatabase = new PeopleDatabase(LOGGER);
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
