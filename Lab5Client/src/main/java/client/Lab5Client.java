package client;

import client.commandline.CommandLineHandlerClient;
import common.commandline.CommandLineHandler;
import common.util.UtilFunctions;

import java.util.logging.Logger;

public class Lab5Client {

    public final static Logger LOGGER = UtilFunctions.getLogger(Lab5Client.class, "client");

    public static void main(String[] args) {
        CommandLineHandler cmd = CommandLineHandlerClient.getClientCommandLine();
        cmd.start();
    }
}
