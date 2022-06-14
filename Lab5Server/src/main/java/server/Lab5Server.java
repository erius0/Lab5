package server;

import common.collection.PeopleCollection;
import common.commandline.CommandLineHandler;
import common.commandline.pdcommands.PeopleDatabaseCommands;
import common.net.ConnectionProperties;
import common.util.UtilFunctions;
import server.commandline.CommandLineHandlerServer;
import server.net.UDPServer;

import java.util.logging.Logger;

public class Lab5Server {

    public final static Logger LOGGER = UtilFunctions.getLogger(Lab5Server.class, "server");

    public static void main(String[] args) {
        PeopleCollection peopleCollection = new PeopleCollection();
        CommandLineHandler cmd = CommandLineHandlerServer.getServerCommandLine();
        PeopleDatabaseCommands.peopleCollection = peopleCollection;
        UDPServer udp = new UDPServer(ConnectionProperties.getPort(), LOGGER);
        udp.connect();

        Runtime.getRuntime().addShutdownHook(new Thread());

        cmd.start();

        Thread thread = new Thread(() -> {
            while (cmd.isActive())
                udp.receive(peopleCollection);
        });
        thread.start();
    }
}
