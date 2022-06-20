package server;

import common.commandline.CommandLineHandler;
import common.commandline.pdcommands.PeopleDatabaseCommands;
import common.net.ConnectionProperties;
import common.util.UtilFunctions;
import server.commandline.CommandLineHandlerServer;
import server.net.UDPServer;

import java.sql.SQLException;
import java.util.logging.Logger;

public class Lab5Server {

    public final static Logger LOGGER = UtilFunctions.getLogger(Lab5Server.class, "server");

    static {
        CommandLineHandler.clearScreen();
    }

    public static void main(String[] args) {
        CommandLineHandler cmd = CommandLineHandlerServer.getServerCommandLine();
        UDPServer udp = new UDPServer(ConnectionProperties.getPort(), LOGGER);
        udp.connect();

        try {
            PeopleDatabaseCommands.peopleCollection = udp.loadFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.severe("Ошибка при получении коллекции из базы данных");
        }

        // Runtime.getRuntime().addShutdownHook(new Thread());

        Thread conThread = new Thread(() -> {
            while (true)
                udp.receive();
        });
        conThread.setDaemon(true);
        conThread.start();

        cmd.start();
    }
}
