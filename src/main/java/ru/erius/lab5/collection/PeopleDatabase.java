package ru.erius.lab5.collection;

import lombok.Getter;
import ru.erius.lab5.data.Person;
import ru.erius.lab5.parser.LocalDateAdapter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.TreeSet;

@XmlRootElement
public class PeopleDatabase implements Database {

    private final static String
            ENV_VAR = "LAB5_PATH",
            DEFAULT_FILE_NAME = "lab5.xml",
            TYPE = "TreeSet";

    @XmlTransient
    private JAXBContext context;
    @XmlTransient
    private File file;
    @Getter @XmlElement(name = "person")
    private TreeSet<Person> collection = new TreeSet<>();
    @Getter @XmlJavaTypeAdapter(LocalDateAdapter.class) @XmlElement(name = "initDate")
    private LocalDate initDate = LocalDate.now();

    {
        try {
            this.context = JAXBContext.newInstance(PeopleDatabase.class);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public String info() {
        return String.format("Тип коллекции: %s \n" +
                        "Дата инициализации: %s \n" +
                        "Количество элементов: %d \n",
                        TYPE, this.initDate, this.collection.size());
    }

    @Override
    public void load() throws Database.DatabaseLoadFailedException {
        System.out.println("Инициализация коллекции из файла...");

        String path = System.getenv(ENV_VAR);
        if (path == null)
            throw new DatabaseLoadFailedException("Не найдена переменная окружения LAB5_PATH");
        file = new File(path);

        if (!file.exists())
            throw new DatabaseLoadFailedException("Файл %s не был найден. Поменяйте значение переменной окружения LAB5_PATH", path);
        if (!file.canRead())
            throw new DatabaseLoadFailedException("У вас нет прав на чтение файла %s", path);
        if (!file.canWrite())
            throw new DatabaseLoadFailedException("У вас нет прав на запись в файл %s", path);
        if (file.isFile())
            System.out.println("Файл успешно найден");
        else
            file = createFile(file);

        try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            PeopleDatabase pd = (PeopleDatabase) unmarshaller.unmarshal(file);
            this.collection = pd.collection;
            this.initDate = pd.initDate;
        } catch (JAXBException e) {
            throw new DatabaseLoadFailedException("Не удалось загрузить коллекцию из файла %s", file.getPath(), e);
        }
        System.out.println("Инициализация успешно выполнена");
    }

    @Override
    public void save() throws Database.DatabaseSaveFailedException {
        if (file == null || context == null)
            throw new DatabaseSaveFailedException("Не удалось сохранить коллекцию");
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(this, file);
        } catch (JAXBException e) {
            throw new DatabaseSaveFailedException("Не удалось сохранить коллекцию в файл %s", file.getPath(), e);
        }
    }

    private File createFile(File file) {
        System.out.println("Директория успешно найдена");
        file = new File(file.getAbsolutePath() + "/" + DEFAULT_FILE_NAME);
        boolean exists;
        try {
            exists = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Что-то пошло не так");
            System.exit(-1);
            return null;
        }
        System.out.println(exists ? "Файл не найден, создание файла " + DEFAULT_FILE_NAME : "Файл успешно найден");
        return file;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PeopleDatabase(\n");
        this.collection.forEach(p -> sb.append("\t").append(p).append("\n"));
        sb.append(")");
        return sb.toString();
    }
}
