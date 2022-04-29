package client.net;

import common.commandline.Executable;
import common.commandline.response.CommandResult;
import common.commandline.response.DefaultResponse;

import java.io.*;
import java.net.*;

public class UDPClient {

    private String hostname;
    private final int port;
    private InetAddress address;
    private DatagramSocket socket;
    boolean available = false;

    public UDPClient(int port) {
        this.port = port;
    }

    public UDPClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void connect() {
        try {
            address = hostname == null ? InetAddress.getLocalHost() : InetAddress.getByName(hostname);
            socket = new DatagramSocket();
            socket.setSoTimeout(10000);
            available = true;
        } catch (UnknownHostException | SocketException e) {
            System.err.println("Ошибка при подключении к серверу, хост не найден");
            available = false;
        }
    }

    public void disconnect() {
        if (!available) return;
        socket.close();
        available = false;
    }

    public CommandResult send(Executable executable, Object[] args) {
        if (!available) return new CommandResult(null, DefaultResponse.HOST_NOT_FOUND);
        byte[] buffer;
        try (
                ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream)
        ) {
            objectOutputStream.writeObject(executable);
            objectOutputStream.writeObject(args);
            buffer = byteOutputStream.toByteArray();
            DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(request);
        } catch (IOException e) {
            return new CommandResult("Нет ответа от сервера", DefaultResponse.SERVER_ERROR);
        }
        return receive();
    }

    public CommandResult receive() {
        byte[] buffer = new byte[65535];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
        try (ByteArrayInputStream byteInputStream = new ByteArrayInputStream(buffer)) {
            socket.receive(response);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
            CommandResult result = (CommandResult) objectInputStream.readObject();
            objectInputStream.close();
            return result;
        } catch (SocketTimeoutException e) {
            return new CommandResult("Сервер не отвечает", DefaultResponse.SERVER_ERROR);
        } catch (IOException e) {
            return new CommandResult("Нестабильное соединении", DefaultResponse.SERVER_ERROR);
        } catch (ClassNotFoundException e) {
            return new CommandResult("Не удалось преобразовать результат, не существует нужного класса", DefaultResponse.CLASS_NOT_FOUND);
        } catch (ClassCastException e) {
            return new CommandResult("Не удалось преобразовать результат, ожидался объект другого типа", DefaultResponse.TYPE_ERROR);
        }
    }
}
