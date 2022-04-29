package server.net;

import common.collection.PeopleDatabase;
import common.commandline.Executable;
import common.commandline.response.CommandResult;
import common.commandline.response.DefaultResponse;

import java.io.*;
import java.net.*;
import java.util.logging.Logger;

public class UDPServer {

    private String hostname;
    private final int port;
    private DatagramSocket socket;
    private final Logger logger;

    public UDPServer(int port, Logger logger) {
        this.port = port;
        this.logger = logger;
    }

    public UDPServer(String hostname, int port, Logger logger) {
        this.hostname = hostname;
        this.port = port;
        this.logger = logger;
    }

    public boolean connect() {
        logger.info("Подключаемся...");
        try {
            InetAddress address = hostname == null ? InetAddress.getLocalHost() : InetAddress.getByName(hostname);
            socket = new DatagramSocket(port);
            logger.info("Подключение установлено");
            return true;
        } catch (UnknownHostException e) {
            logger.severe("Хост не найден");
            return false;
        } catch (SocketException e) {
            logger.severe("Не удалось открыть сокет");
            return false;
        }
    }

    public void disconnect() {
        logger.info("Разрываем соединение...");
        socket.close();
        logger.info("Соединение разорвано");
    }

    public void send(CommandResult result, InetAddress address, int port) {
        byte[] buffer;
        try (
                ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream)
        ) {
            objectOutputStream.writeObject(result);
            buffer = byteOutputStream.toByteArray();
            DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(request);
        } catch (IOException e) {
            logger.severe("Не удалось отправить данные клиенту, неполадки в соединении");
        }
        logger.info("Результат отправлен клиенту");
    }

    public void receive(PeopleDatabase peopleDatabase) {
        logger.info("Ожидаем отправки данных от клиента...");
        byte[] buffer = new byte[65535];
        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
        CommandResult result;
        try (ByteArrayInputStream byteInputStream = new ByteArrayInputStream(buffer)) {
            socket.receive(request);
            logger.info("Данные получены, выполняем команду...");
            ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
            Executable command = (Executable) objectInputStream.readObject();
            Object[] args = (Object[]) objectInputStream.readObject();
            objectInputStream.close();
            if (args[0] == null) args[0] = peopleDatabase;
            result = command.execute(args);
        } catch (IOException e) {
            logger.info("Не удалось преобразовать полученные данные, данные были повреждены во время передачи");
            result = new CommandResult(null, DefaultResponse.SERVER_ERROR);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.info("Не удалось преобразовать полученные данные, классов полученных объектов не существует");
            result = new CommandResult(null, DefaultResponse.CLASS_NOT_FOUND);
        } catch (ClassCastException e) {
            logger.info("Не удалось преобразовать полученные данные, ожидались объекты другого типа");
            result = new CommandResult(null, DefaultResponse.TYPE_ERROR);
        }
        logger.info("Команда выполнена, отправляем результат клиенту...");
        send(result, request.getAddress(), request.getPort());
    }
}
