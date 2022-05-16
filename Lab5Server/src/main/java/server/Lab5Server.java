package server;

import common.collection.Database;
import common.collection.PeopleDatabase;
import common.commandline.CommandLineHandler;
import common.commandline.pdcommands.PeopleDatabaseCommands;
import common.parser.ConnectionProperties;
import common.util.UtilFunctions;
import server.commandline.CommandLineHandlerServer;
import server.net.UDPServer;

import java.util.logging.Logger;

public class Lab5Server {

    public final static Logger LOGGER = UtilFunctions.getLogger(Lab5Server.class, "server");

    public static void main(String[] args) {
        PeopleDatabase peopleDatabase = new PeopleDatabase(LOGGER);
        try {
            peopleDatabase.load();
        } catch (Database.DatabaseLoadFailedException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        CommandLineHandler cmd = CommandLineHandlerServer.getServerCommandLine();
        PeopleDatabaseCommands.peopleDatabase = peopleDatabase;
        UDPServer udp = new UDPServer(ConnectionProperties.getPort(), LOGGER);
        if (!udp.connect()) System.exit(-1);

        Thread thread = new Thread(() -> {
            while (true)
                udp.receive(peopleDatabase);
        });
        thread.setDaemon(true);
        thread.start();

        cmd.start();
    }
}
