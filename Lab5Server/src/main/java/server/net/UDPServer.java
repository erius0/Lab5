package server.net;

import common.collection.PeopleCollection;
import common.commandline.Command;
import common.commandline.CommandLineHandler;
import common.commandline.PlaceHolder;
import common.commandline.response.CommandResult;
import common.commandline.response.DefaultResponse;
import common.commandline.response.Response;
import common.data.*;
import common.net.ConnectionProperties;
import common.util.UtilFunctions;
import server.commandline.CommandLineHandlerServer;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.Locale;
import java.util.logging.Logger;

import static common.commandline.pdcommands.PeopleDatabaseCommands.peopleCollection;

public class UDPServer {

    private final int port;
    private DatagramSocket socket;
    private final Logger logger;
    private final static int BUFFER_SIZE = 65_535;
    private static Connection db_connection;

    public UDPServer(int port, Logger logger) {
        this.port = port;
        this.logger = logger;
    }

    public void connect() {
        logger.info("Подключаемся...");
        do {
            try {
                socket = new DatagramSocket(port);
                logger.info("Подключение установлено");
            } catch (SocketException e) {
                e.printStackTrace();
                logger.severe("Не удалось установить соединение, порт занят");
            }
        } while (socket == null);
        logger.info("Подключаемся к базе данных...");
        checkDriver();
        DriverManager.setLoginTimeout(5);
        connectDatabase();
    }

    private void checkDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.severe("Драйвер JDBC для базы данных PostgreSQL не найден");
            System.exit(-1);
        }
    }

    private void connectDatabase() {
        String url = ConnectionProperties.getDbURL();
        CommandLineHandler cmd = CommandLineHandlerServer.getServerCommandLine();
        do {
            String user = cmd.awaitInput("Введите логин:");
            String password = cmd.awaitPassword("Введите пароль:");
            try {
                db_connection = DriverManager.getConnection(url, user, password);
                db_connection.setAutoCommit(false);
            } catch (SQLTimeoutException e) {
                e.printStackTrace();
                logger.severe("Не удалось установить соединение с базой данных, превышено время ожидания");
                System.exit(-1);
            } catch (SQLException e) {
                e.printStackTrace();
                handleSqlException(e);
            }
        } while (db_connection == null);
    }

    private void handleSqlException(SQLException e) {
        Throwable cause = e.getCause();
        if (cause instanceof UnknownHostException) {
            logger.severe("Не удалось установить соединение с базой данных, хост бд не найден");
            System.exit(-1);
        } else if (cause instanceof ConnectException) {
            logger.severe("Не удалось установить соединение с базой данных, хост бд или порт не корректны");
            System.exit(-1);
        } else if (cause instanceof SocketTimeoutException) {
            logger.severe("Не удалось установить соединение с базой данных, превышено время ожидания");
            System.exit(-1);
        } else {
            String firstLine = e.getMessage().split("\n")[0];
            if (firstLine.contains("database")) {
                logger.severe("Не удалось установить соединение с базой данных, бд с заданным именем не найдена");
                System.exit(-1);
            } else {
                logger.severe("Логин или пароль неверны, повторите попытку");
            }
        }
    }

    public void disconnect() {
        logger.info("Разрываем соединение...");
        socket.close();
        logger.info("Соединение разорвано");
    }

    public void send(CommandResult result, InetAddress address, int port) {
        byte[] buffer;
        try (ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream()) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
            objectOutputStream.writeObject(result);
            buffer = byteOutputStream.toByteArray();
            DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(request);
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            logger.severe("Не удалось отправить данные клиенту, клиент не отвечает");
            return;
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("Не удалось отправить данные клиенту, неполадки в соединении");
            return;
        }
        logger.info("Результат отправлен клиенту");
    }

    public void receive() {
        logger.info("Ожидаем отправки данных от клиента...");
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
        CommandResult result;
        try (ByteArrayInputStream byteInputStream = new ByteArrayInputStream(buffer)) {
            socket.receive(request);
            logger.info("Данные получены, десериализуем...");
            ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
            Command command = (Command) objectInputStream.readObject();
            Object[] args = (Object[]) objectInputStream.readObject();
            objectInputStream.close();
            args = PlaceHolder.replacePlaceHoldersWith(args, peopleCollection, db_connection);
            logger.info(String.format("Выполняется команда %s", command.getAlias()));
            result = command.execute(args);
        } catch (IOException e) {
            e.printStackTrace();
            result = onReceiveException("Не удалось преобразовать полученные данные, данные были повреждены во время передачи",
                    DefaultResponse.SERVER_ERROR);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            result = onReceiveException("Не удалось преобразовать полученные данные, классов полученных объектов не существует",
                    DefaultResponse.CLASS_NOT_FOUND);
        } catch (ClassCastException e) {
            e.printStackTrace();
            result = onReceiveException("Не удалось преобразовать полученные данные, ожидались объекты другого типа",
                    DefaultResponse.TYPE_ERROR);
        }
        logger.info(String.format("Команда выполнена с результатом %s, сохраняем и отправляем результат клиенту...", result.getResponse().toString()));
        send(result, request.getAddress(), request.getPort());
    }

    private CommandResult onReceiveException(String msg, Response response) {
        logger.severe(msg);
        return new CommandResult(response.getMsg(), response);
    }

    public PeopleCollection loadFromDatabase() throws SQLException {
        PeopleCollection peopleCollection = new PeopleCollection();
        logger.info("Получаем коллекцию из базы данных...");
        PreparedStatement statement = db_connection.prepareStatement(
                    "SELECT people.id, people.name, coordinates.x, coordinates.y, height, passport, " +
                        "colors.name, countries.name, locations.x, locations.y, locations.z, locations.name, login FROM people " +
                        "JOIN coordinates ON coordinates.id = coordinates_id " +
                        "JOIN locations ON locations.id = location_id " +
                        "JOIN colors ON colors.id = color_id " +
                        "JOIN countries ON countries.id = country_id " +
                        "JOIN users ON users.id = owner_id;"
        );
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            long id = result.getLong(1);
            String name = result.getString(2);
            Coordinates coordinates = new Coordinates(result.getFloat(3), result.getFloat(4));
            Integer height = result.getInt(5);
            String passport = result.getString(6);
            Color color = UtilFunctions.enumOrNull(result.getString(7).toUpperCase(Locale.ROOT), Color.class);
            Country country = UtilFunctions.enumOrNull(result.getString(8).toUpperCase(Locale.ROOT), Country.class);
            Location location = new Location(result.getDouble(9), result.getFloat(10), result.getLong(11), result.getString(12));
            String owner = result.getString(13);
            Person person = new Person(id, name, coordinates, height, passport, color, country, location, owner);
            peopleCollection.getCollection().add(person);
            logger.info("Добавлен человек " + person);
        }
        logger.info("Коллекция успешно получена");
        return peopleCollection;
    }
}
