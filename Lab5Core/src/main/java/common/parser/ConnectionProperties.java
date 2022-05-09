package common.parser;

import common.util.UtilFunctions;

import java.io.*;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class ConnectionProperties {

    private static final Properties properties = new Properties();
    private static final Logger logger = Logger.getLogger("Lab5");
    private static final int DEFAULT_PORT = 1234;
    private static final String DEFAULT_HOST = "localhost";

    static {
        File file = new File("connection.properties");
        try {
            if (file.exists()) {
                logger.info("Файл найден, загружаем настройки подключения...");
            }
            else {
                logger.warning("Файл не найден, создание файла " + file.getName());
                BufferedReader reader = new BufferedReader(new InputStreamReader(ConnectionProperties.class.getClassLoader().getResource("connection.properties").openStream()));
                String content = reader.lines().collect(Collectors.joining("\n"));
                file.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(content);
                writer.close();
                reader.close();
                logger.info("Файл создан, загружаем настройки подключения");
            }
            properties.load(new FileInputStream(file));
            logger.info("Настройки подключения загружены из файла " + file.getName());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static String getHostname() {
        String result = properties.getProperty("hostname");
        if (result == null) {
            logger.severe("Свойство hostname не было найдено, используем значение по умолчанию localhost");
            return DEFAULT_HOST;
        } else return result;
    }

    public static int getPort() {
        String resultStr = properties.getProperty("port");
        if (resultStr == null) {
            logger.severe("Свойство port не было найден, используем значение по умолчанию 1234");
            return DEFAULT_PORT;
        }
        Integer result = UtilFunctions.intOrNull(resultStr);
        if (result == null) {
            logger.severe("Порт должен быть целым числом, используем значение по умолчанию 1234");
            return DEFAULT_PORT;
        }
        return result;
    }
}
