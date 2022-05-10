package server.net;

import common.collection.PeopleDatabase;
import common.commandline.Executable;
import common.commandline.Executables;
import common.commandline.response.CommandResult;
import common.commandline.response.DefaultResponse;
import common.commandline.response.Response;

import java.io.*;
import java.net.*;
import java.util.logging.Logger;

public class UDPServer {

    private final int port;
    private DatagramSocket socket;
    private final Logger logger;
    private final static int BUFFER_SIZE = 65_535;

    public UDPServer(int port, Logger logger) {
        this.port = port;
        this.logger = logger;
    }

    public boolean connect() {
        logger.info("Подключаемся...");
        try {
            socket = new DatagramSocket(port);
            logger.info("Подключение установлено");
            return true;
        } catch (SocketException e) {
            logger.severe("Не удалось установить соединение, порт занят");
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

    public void receive(PeopleDatabase peopleDatabase) {
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
            if (args[0] == null) args[0] = peopleDatabase;
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
        Executables.SAVE.getExecutable().execute(new Object[]{ peopleDatabase });
        send(result, request.getAddress(), request.getPort());
    }
}
