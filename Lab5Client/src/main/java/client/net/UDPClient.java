package client.net;

import common.commandline.Executable;
import common.commandline.response.CommandResult;
import common.commandline.response.DefaultResponse;
import common.commandline.response.Response;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UDPClient {

    private String hostname;
    private int port;
    private InetSocketAddress address;
    private DatagramChannel datagramChannel;
    private boolean isAvailable = false;
    private final static String LOCALHOST = "localhost";
    private final static int    BUFFER_SIZE = 65_535,
                                TIMES_TO_TRY_READ = 10,
                                READ_ATTEMPT_DELAY_MS = 500;

    public UDPClient(int port) {
        this.port = port;
    }

    public UDPClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void connect() {
        try {
            if (hostname == null) hostname = LOCALHOST;
            datagramChannel = DatagramChannel.open();
            address = new InetSocketAddress(hostname, port);
            datagramChannel.bind(new InetSocketAddress(0));
            datagramChannel.configureBlocking(false);
            if (address.isUnresolved()) {
                System.err.println("Адреса " + hostname + " не существует, укажите другой адрес");
                return;
            }
        } catch (SocketException e) {
            System.err.println("Указанный порт подключения занят, укажите другой порт");
        } catch (IOException e) {
            System.err.println("Что-то пошло не так при соединении с сервером");
        }
        isAvailable = true;
    }

    public void disconnect() {
        if (!isAvailable) return;
        try {
            datagramChannel.close();
        } catch (IOException e) {
            System.err.println("Что-то пошло не так во время разрыва соединения с сервером");
        }
        isAvailable = false;
    }

    public CommandResult send(Executable executable, Object[] args) {
        if (!isAvailable) {
            Response response = DefaultResponse.HOST_NOT_FOUND;
            return new CommandResult(response.getMsg(), response);
        }
        try (ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(BUFFER_SIZE)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
            objectOutputStream.writeObject(executable);
            objectOutputStream.writeObject(args);
            ByteBuffer buffer = ByteBuffer.wrap(byteOutputStream.toByteArray());
            datagramChannel.send(buffer, address);
        } catch (IOException e) {
            return new CommandResult("Нет ответа от сервера", DefaultResponse.SERVER_ERROR);
        }
        return receive();
    }

    public CommandResult receive() {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        boolean dataReceived = false;
        try {
            for (int i = 0; i < TIMES_TO_TRY_READ; i++) {
                if (datagramChannel.receive(buffer) != null) {
                    dataReceived = true;
                    break;
                }
                Thread.sleep(READ_ATTEMPT_DELAY_MS);
            }
        } catch (IOException e) {
            return new CommandResult("Нестабильное соединение", DefaultResponse.SERVER_ERROR);
        } catch (InterruptedException e) {
            return new CommandResult("Получение данных было прервано", DefaultResponse.UNKNOWN);
        }
        if (!dataReceived)
            return new CommandResult("Сервер не отвечает", DefaultResponse.SERVER_ERROR);
        try (ByteArrayInputStream byteInputStream = new ByteArrayInputStream(buffer.array())) {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
            CommandResult result = (CommandResult) objectInputStream.readObject();
            objectInputStream.close();
            return result;
        } catch (IOException e) {
            return new CommandResult("Данные были повреждены", DefaultResponse.SERVER_ERROR);
        } catch (ClassNotFoundException e) {
            return new CommandResult("Не удалось преобразовать результат, не существует нужного класса", DefaultResponse.CLASS_NOT_FOUND);
        } catch (ClassCastException e) {
            e.printStackTrace();
            return new CommandResult("Не удалось преобразовать результат, ожидался объект другого типа", DefaultResponse.TYPE_ERROR);
        }
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}
