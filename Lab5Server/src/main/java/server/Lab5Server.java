package server;

import common.collection.Database;
import common.collection.PeopleDatabase;
import common.parser.ConnectionProperties;
import server.net.UDPServer;

import java.util.logging.Logger;

public class Lab5Server {

    public final static Logger logger = Logger.getLogger("Lab5Server");


    public static void main(String[] args) {
        PeopleDatabase peopleDatabase = new PeopleDatabase(logger);
        try {
            peopleDatabase.load();
        } catch (Database.DatabaseLoadFailedException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        UDPServer udp = new UDPServer(ConnectionProperties.getPort(), logger);
        if (!udp.connect()) System.exit(-1);
        while (true)
            udp.receive(peopleDatabase);
    }
}
