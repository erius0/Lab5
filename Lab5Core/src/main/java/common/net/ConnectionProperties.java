package common.net;

import common.util.UtilFunctions;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class ConnectionProperties {

    public static final Properties properties = new Properties();
    public static final Logger logger = UtilFunctions.getLogger(ConnectionProperties.class, "common");
    public static final int     DEFAULT_PORT = 1234,
                                DEFAULT_DB_PORT = 5432;
    public static final String  DEFAULT_HOST = "localhost",
                                DEFAULT_DB_HOST = "pg",
                                DEFAULT_DB_NAME = "studs";
    public static final String FILE_NAME = "connection.properties";

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
        return getTransformedProperty("hostname", DEFAULT_HOST);
    }

    public static void setHostname(String hostname) {
        setProperty("hostname", hostname);
    }

    public static int getPort() {
        return getTransformedProperty("port", DEFAULT_PORT, s -> {
            Integer result = UtilFunctions.intOrNull(s);
            return result == null ? DEFAULT_PORT : result;
        });
    }

    public static void setPort(int port) {
        setProperty("port", String.valueOf(port));
    }

    public static String getDbURL() {
        String  host = getTransformedProperty("db_host", DEFAULT_DB_HOST),
                port = getTransformedProperty("db_port", DEFAULT_DB_PORT, s -> {
                    Integer result = UtilFunctions.intOrNull(s);
                    if (result == null) {
                        logger.info("db_port должен быть целым числом, используем значение по умолчанию " + DEFAULT_DB_PORT);
                        return DEFAULT_DB_PORT;
                    }
                    return result;
                }).toString(),
                name = getTransformedProperty("db_name", DEFAULT_DB_NAME);
        return String.format("jdbc:postgresql://%s:%s/%s", host, port, name);
    }

    private static String getTransformedProperty(String key, String def) {
        return getTransformedProperty(key, def, s -> s);
    }

    private static <T> T getTransformedProperty(String key, T def, Function<String, T> transform) {
        String property = properties.getProperty(key);
        if (property == null) {
            logger.info(String.format("Свойство %s не найдено, используем значение по умолчанию %s", key, def));
            return def;
        }
        return transform.apply(property);
    }

    private static void setProperty(String key, String value) {
        properties.setProperty(key, value);
        File file = new File(FILE_NAME);
        try (FileWriter writer = new FileWriter(file)) {
            properties.store(writer, "Changed by user");
            logger.info(String.format("Свойство %s успешно изменено", key));
        } catch (IOException e) {
            logger.severe(String.format("Не удалось сохранить свойство %s", key));
        }
    }
}
