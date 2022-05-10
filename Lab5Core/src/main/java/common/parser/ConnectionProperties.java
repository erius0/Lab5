package common.parser;

import common.util.UtilFunctions;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class ConnectionProperties {

    private static final Properties properties = new Properties();
    private static final Logger logger = UtilFunctions.getLogger(ConnectionProperties.class, "common");
    private static final int DEFAULT_PORT = 1234;
    private static final String DEFAULT_HOST = "localhost";
    private static final String FILE_NAME = "connection.properties";

    static {
        File file = new File(FILE_NAME);
        try {
            if (file.exists()) {
                logger.info("Файл найден, загружаем настройки подключения...");
            } else {
                logger.warning("Файл не найден, создание файла " + FILE_NAME);
                URL url = ConnectionProperties.class.getClassLoader().getResource(FILE_NAME);
                if (url == null) {
                    logger.severe("Jar файл поврежден, переустановите программу");
                    System.exit(-1);
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                String content = reader.lines().collect(Collectors.joining("\n"));
                boolean wasCreated = file.createNewFile();
                if (!wasCreated) {
                    logger.severe("Невозможно создать файл " + FILE_NAME + ", недостаточно прав");
                }
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(content);
                writer.close();
                reader.close();
                logger.info("Файл создан, загружаем настройки подключения");
            }
            properties.load(new FileInputStream(file));
            logger.info("Настройки подключения загружены из файла " + FILE_NAME);
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

    public static void setHostname(String hostname) {
        properties.setProperty("hostname", hostname);
        File file = new File(FILE_NAME);
        try (FileWriter writer = new FileWriter(file)) {
            properties.store(writer, "Changed by user");
            logger.info("Адрес успешно изменен");
        } catch (IOException e) {
            logger.severe("Не удалось сохранить измененные значения");
        }
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

    public static void setPort(int port) {
        properties.setProperty("port", String.valueOf(port));
        File file = new File(FILE_NAME);
        try (FileWriter writer = new FileWriter(file)) {
            properties.store(writer, "Changed by user");
            logger.info("Порт успешно изменен");
        } catch (IOException e) {
            logger.severe("Не удалось сохранить измененные значения");
        }
    }
}
