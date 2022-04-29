package common.collection;

import lombok.Getter;
import common.data.Person;
import common.parser.Adapters;

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
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Класс базы данных людей, реализующий интерфейс Database
 *
 * @see Database
 */
@XmlRootElement
public class PeopleDatabase implements Database {

    private final static String
            ENV_VAR = "LAB5_PATH",
            DEFAULT_FILE_NAME = "lab5.xml",
            TYPE = "TreeSet";

    @XmlTransient
    private Logger logger = Logger.getLogger("Lab5Client");
    @XmlTransient
    private JAXBContext context;
    @XmlTransient
    private File file;
    @Getter
    @XmlElement(name = "person")
    private TreeSet<Person> collection = new TreeSet<>();
    @Getter
    @XmlJavaTypeAdapter(Adapters.LocalDateAdapter.class)
    @XmlElement(name = "initDate")
    private LocalDate initDate = LocalDate.now();

    {
        try {
            this.context = JAXBContext.newInstance(PeopleDatabase.class);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private PeopleDatabase() {}

    public PeopleDatabase(Logger logger) {
        this.logger = logger;
    }

    public String info() {
        return String.format("Тип коллекции: %s \n" +
                        "Дата инициализации: %s \n" +
                        "Количество элементов: %d \n",
                TYPE, this.initDate, this.collection.size());
    }

    /**
     * Метод, инициализирующий базу данных из файла, находящемся по пути, указанном в
     * переменной окружения {@link #ENV_VAR}
     *
     * @throws Database.DatabaseLoadFailedException если переменная окружения {@link #ENV_VAR} не задана,
     *                                              файла не существует, либо отсутствуют права на запись или чтение
     */
    @Override
    public void load() throws Database.DatabaseLoadFailedException {
        logger.info("Инициализация коллекции из файла...");

        String path = System.getenv(ENV_VAR);
        boolean envSet = true;
        if (path == null) {
            logger.warning("Не найдена переменная окружения LAB5_PATH, создание файла в директории jar файла");
            path = ".";
            envSet = false;
        }
        File file = new File(path);

        if (!file.exists() && envSet) {
            logger.warning("Файл " + file.getAbsolutePath() + " не был найден, создание файла в директории jar файла");
            file = new File(".");
        }
        if (file.isDirectory())
            file = createFile(file);
        if (!file.canRead())
            throw new DatabaseLoadFailedException("У вас нет прав на чтение файла %s", file.getAbsolutePath());
        if (!file.canWrite())
            throw new DatabaseLoadFailedException("У вас нет прав на запись в файл %s", file.getAbsolutePath());
        this.file = file;

        try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            PeopleDatabase pd = (PeopleDatabase) unmarshaller.unmarshal(file);
            this.collection = pd.collection;
            this.initDate = pd.initDate;
            logger.info("Инициализация успешно выполнена");
        } catch (JAXBException e) {
            logger.severe("Не удалось загрузить коллекцию из файла " + file.getAbsolutePath() + ", он пуст, либо нарушена структура xml");
        }
    }

    /**
     * Метод, сохраняющий базу данных в файл, находящемся по пути, указанном в
     * переменной окружения {@link #ENV_VAR}
     *
     * @throws Database.DatabaseSaveFailedException если переменная окружения {@link #ENV_VAR} не задана,
     *                                              файла не существует, либо отсутствуют права на запись или чтение, или если структура xml файла
     *                                              была каким-либо образом нарушена
     */
    @Override
    public void save() throws Database.DatabaseSaveFailedException {
        if (!file.exists()) {
            logger.warning("Файла " + file.getAbsolutePath() + " не существует, возможно он был удален, пытаюсь воссоздать файл...");
            try {
                file.createNewFile();
                logger.info("Файл был успешно создан");
            } catch (IOException e) {
                throw new DatabaseSaveFailedException("Не удается создать файл заново, пожалуйста, сделайте это вручную и попробуйте еще раз");
            }
        }
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(this, file);
        } catch (JAXBException e) {
            throw new DatabaseSaveFailedException("Не удалось сохранить коллекцию в файл %s, формат xml файла был нарушен", file.getPath(), e);
        }
    }

    private File createFile(File file) {
        logger.info("Директория успешно найдена");
        file = new File(file.getAbsolutePath() + "/" + DEFAULT_FILE_NAME);
        boolean exists;
        try {
            exists = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("Что-то пошло не так");
            System.exit(-1);
            return null;
        }
        logger.info(exists ? "Файл не найден, создание файла " + DEFAULT_FILE_NAME : "Файл успешно найден");
        return file;
    }

    @Override
    public String toString() {
        String result = "PeopleDatabase(";
        result += this.collection.stream().map(Person::toString).collect(Collectors.joining(", "));
        result += ")";
        return result;
    }
}
