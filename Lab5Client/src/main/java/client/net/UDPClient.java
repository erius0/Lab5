package client.net;

import client.commandline.CommandLineHandlerClient;
import common.commandline.Command;
import common.commandline.CommandLineHandler;
import common.commandline.PlaceHolder;
import common.commandline.commands.LogInCommand;
import common.commandline.commands.SignUpCommand;
import common.commandline.response.CommandResult;
import common.commandline.response.DefaultResponse;
import common.commandline.response.SqlResponse;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.Base64;
import java.util.Locale;

public class UDPClient {

    private static MessageDigest md;

    static {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Алгоритм хеширования MD5 не найден");
            System.exit(-1);
        }
    }

    private String hostname;
    private int port;
    private InetSocketAddress address;
    private DatagramChannel datagramChannel;
    private final static String LOCALHOST = "localhost";
    private final static int BUFFER_SIZE = 65_535,
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
                System.exit(-1);
            }
        } catch (SocketException e) {
            System.err.println("Указанный порт подключения занят, укажите другой порт");
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("Что-то пошло не так при соединении с сервером");
            System.exit(-1);
        }
        this.logIn();
    }

    private void logIn() {
        CommandLineHandler cmd = CommandLineHandlerClient.getClientCommandLine();
        String signUpAnswer = cmd.awaitInput("Имеется ли у вас аккаунт? (Y - да, N - нет):");
        boolean accExists = signUpAnswer.toLowerCase(Locale.ROOT).equals("y");
        if (!accExists) signUp();
        System.out.println("Вход в существующий аккаунт");
        CommandResult result;
        String login;
        do {
            login = cmd.awaitInput("Введите логин:", "Логин не может быть пустым", s -> !s.isEmpty());
            String password = cmd.awaitPassword("Введите пароль:");
            String passwordMD5 = encodePassword(password);
            Command logIn = new LogInCommand();
            Object[] args = new Object[]{login, passwordMD5, PlaceHolder.of(Connection.class)};
            result = this.send(logIn, args);
            System.out.println(result.getValue());
        } while (result.getResponse() != SqlResponse.OK);
        CommandLineHandler.setUser(login);
        System.out.println("Вход выполнен, добро пожаловать, " + login);
    }

    private void signUp() {
        CommandLineHandler cmd = CommandLineHandlerClient.getClientCommandLine();
        System.out.println("Регистрация нового пользователя");
        CommandResult result;
        do {
            String login = cmd.awaitInput("Введите логин:", "Логин не может быть пустым", s -> !s.isEmpty());
            String password = cmd.awaitPassword("Введите пароль:");
            String passwordMD5 = encodePassword(password);
            Command signUp = new SignUpCommand();
            Object[] args = new Object[]{login, passwordMD5, PlaceHolder.of(Connection.class)};
            result = this.send(signUp, args);
            System.out.println(result.getValue());
        } while (result.getResponse() != SqlResponse.OK);
        System.out.println("Аккаунт успешно создан");
    }

    private String encodePassword(String password) {
        md.update(password.getBytes(StandardCharsets.UTF_8));
        byte[] bytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        return sb.toString();
    }

    public void disconnect() {
        try {
            datagramChannel.close();
        } catch (IOException e) {
            System.err.println("Что-то пошло не так во время разрыва соединения с сервером");
        }
    }

    public CommandResult send(Command command, Object[] args) {
        try (ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(BUFFER_SIZE)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
            objectOutputStream.writeObject(command);
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
}
