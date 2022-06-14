package server.net;

import common.collection.PeopleCollection;
import common.commandline.Executable;
import common.commandline.response.CommandResult;
import common.commandline.response.DefaultResponse;
import common.commandline.response.Response;
import common.net.ConnectionProperties;
import server.sql.SQLQueries;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.logging.Logger;

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
                logger.severe("Не удалось установить соединение, порт занят");
            }
        } while (socket == null);
        logger.info("Подключаемся к базе данных...");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            logger.severe("Драйвер JDBC для базы данных PostgreSQL не найден");
            System.exit(-1);
        }
        DriverManager.setLoginTimeout(5);
        String url = ConnectionProperties.getDbURL();
        do {
            Console console = System.console();
            if (console == null) {
                logger.severe("Не удалось получить консоль");
                System.exit(-1);
            }
            String user = console.readLine("Введите логин: ");
            char[] passwordBuff = console.readPassword("Ввелите пароль: ");
            String password = passwordBuff == null ? null : new String(passwordBuff);
            try {
                if (user == null || password == null) logger.severe("Логин или пароль неверны, повторите попытку");
                else db_connection = DriverManager.getConnection(url, user, password);
                db_connection.setAutoCommit(false);
            } catch (SQLTimeoutException e) {
                logger.severe("Не удалось установить соединение с базой данных, время ожидание превышено");
                System.exit(-1);
            } catch (SQLException e) {
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
            logger.severe("Не удалось отправить данные клиенту, клиент не отвечает");
            return;
        } catch (IOException e) {
            logger.severe("Не удалось отправить данные клиенту, неполадки в соединении");
            return;
        }
        logger.info("Результат отправлен клиенту");
    }

    public void receive(PeopleCollection peopleCollection) {
        logger.info("Ожидаем отправки данных от клиента...");
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
        CommandResult result;
        try (ByteArrayInputStream byteInputStream = new ByteArrayInputStream(buffer)) {
            socket.receive(request);
            logger.info("Данные получены, выполняем команду...");
            ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
            Executable command = (Executable) objectInputStream.readObject();
            Object[] args = (Object[]) objectInputStream.readObject();
            objectInputStream.close();
            String query = SQLQueries.getQueryByExecutable(command);
            boolean dbUpdated = updateDatabase(query);
            if (args[0] == null) args[0] = peopleCollection;
            result = command.execute(args);
        } catch (IOException e) {
            logger.severe("Не удалось преобразовать полученные данные, данные были повреждены во время передачи");
            Response response = DefaultResponse.SERVER_ERROR;
            result = new CommandResult(response.getMsg(), response);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.severe("Не удалось преобразовать полученные данные, классов полученных объектов не существует");
            Response response = DefaultResponse.CLASS_NOT_FOUND;
            result = new CommandResult(response.getMsg(), response);
        } catch (ClassCastException e) {
            logger.severe("Не удалось преобразовать полученные данные, ожидались объекты другого типа");
            Response response = DefaultResponse.TYPE_ERROR;
            result = new CommandResult(response.getMsg(), response);
        }
        logger.info("Команда выполнена, сохраняем и отправляем результат клиенту...");
        send(result, request.getAddress(), request.getPort());
    }

    public static boolean updateDatabase(String query) {
        PreparedStatement statement = db_connection.prepareStatement(query);
        statement.setQueryTimeout(5);

    }
}
