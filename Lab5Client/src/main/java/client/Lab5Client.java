package client;

import client.commandline.CommandLineHandlerClient;
import client.net.UDPClient;
import common.commandline.CommandLineHandler;
import common.net.ConnectionProperties;
import common.util.UtilFunctions;

import java.util.logging.Logger;

public class Lab5Client {

    public final static Logger LOGGER = UtilFunctions.getLogger(Lab5Client.class, "client");

    static {
        CommandLineHandler.clearScreen();
    }

    public final static UDPClient UDP = new UDPClient(ConnectionProperties.getHostname(), ConnectionProperties.getPort());

    public static void main(String[] args) {
        CommandLineHandler cmd = CommandLineHandlerClient.getClientCommandLine();
        UDP.connect();
        cmd.start();
    }
}
