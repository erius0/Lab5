package ru.erius.lab5.collection;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Коллекция PersonTreeSet, использующая в основе своей работы коллекцию {@link TreeSet TreeSet},
 * имеет возможности записи коллекции в файл и чтения её же из файла
 */
public class PersonTreeSet {
    /**
     * Название переменной окружения, где должен быть указан путь к файлу
     */
    private final static String ENV_VAR = "PTS_PATH";
    /**
     * Тип коллекции, используется для вывода информации
     */
    private final static String TYPE = "TreeSet";

    /**
     * Файл для записи и чтения коллекции, является null если путь к файлу некорректный
     */
    private final File file = initFile();
    /**
     * Логические переменные, определяющие возможность программы записывать в файл или читать из него
     */
    private boolean canWrite = true, canRead = true;
    /**
     * Коллекция {@link TreeSet TreeSet}, лежащая в основе работы класса
     */
    private final TreeSet<Person> set = new TreeSet<>();
    /**
     * Дата инициализации коллекции
     */
    private final LocalDate creationDate;

    /**
     * Конструктор без параметров, инициализирует коллекцию из файла (если есть возможность)
     * и задаёт дату инициализации
     */
    public PersonTreeSet() {
        this.creationDate = LocalDate.now();
        if (this.file != null && this.canRead)
            this.initTreeSet();
    }

    /**
     * Метод, возвращающий файл для записи и чтения коллекции,
     * возвращает null если переменной окружении не существует,
     * файла не существует, файл является директорией
     * или имеет расширение, не являющееся .xml
     *
     * Устанавливает соответствующим полям {@link #canRead canRead} и {@link #canWrite canWrite} значения false,
     * если нет прав на чтение или запись
     *
     * @return Файл, если все условия соблюдены, иначе null
     */
    public File initFile() {
        String path = System.getenv(ENV_VAR);
        if (path == null) {
            System.err.println("Переменная окружения " + ENV_VAR + " (путь к файлу) не задана, возможность сохранения и загрузки коллекции в файл отключена");
            return null;
        }

        File file = new File(path);
        if (!file.exists()) {
            System.err.println("Файла по пути " + path + " не существует, возможность сохранения и загрузки коллекции в файл отключена");
            return null;
        }

        if (!file.isFile()) {
            System.err.println("В " + ENV_VAR + " записан путь к директории, возможность сохранения и загрузки коллекции в файл отключена");
            return null;
        }

        String ext = this.getFileExtension(file.getPath());
        if (ext == null || !ext.equals("xml")) {
            System.err.println("Файл должен иметь расширение xml, возможность сохранения и загрузки коллекции в файл отключена");
            return null;
        }

        if (!file.canWrite()) {
            System.err.println("Запись в этот файл невозможна, возможность сохранения коллекции файл отключена");
            this.canWrite = false;
        }

        if (!file.canRead()) {
            System.err.println("Чтение из этого файл невозможно, возможность загрузки коллекции из файла отключена");
            this.canRead = false;
        }

        System.out.println("Файл для сохранения и загрузки коллекции был успешно найден");
        System.out.println("Путь: " + path);

        return file;
    }

    /**
     * Метод инициализации коллекции из файла,
     * читает содержимое файла и передаёт его в метод {@link Person#peopleFromString(String)},
     * элементы полученного списка добавляются в коллекцию
     */
    private void initTreeSet() {
        StringBuilder fileContents = new StringBuilder();
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(this.file))) {
            int read;
            do {
                char[] buffer = new char[8192];
                read = reader.read(buffer);
                fileContents.append(buffer);
            } while (read != -1);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Не удалось инициализировать коллекцию из файла");
            return;
        }
        String contents = fileContents.toString();
        List<Person> people = Person.peopleFromString(contents);
        this.set.addAll(people);
        System.out.println("Коллекция успешно инициализирована из файла");
    }

    public File getFile() {
        return file;
    }

    /**
     * Метод, возвращающий расширение файла
     *
     * @param path Строка, в которой записан путь к файлу
     *
     * @return Расширение файла или null, если путь к файлу некорректный
     */
    private String getFileExtension(String path) {
        String[] split = path.split("\\.", 2);
        return split.length > 1 ? split[1] : null;
    }

    /**
     * Метод, возвращающий строку с информацией о коллекции
     *
     * @return Строка с информацией о коллекции
     */
    public String info() {
        return String.format("Тип коллекции: %s \n" +
                        "Дата инициализации: %s \n" +
                        "Количество элементов: %d \n" +
                        "Путь к файлу хранения: %s",
                PersonTreeSet.TYPE, this.creationDate, this.set.size(), this.file);
    }

    /**
     * Метод добавления нового элемента в коллекцию
     *
     * @param person Элемент для добавления
     *
     * @return true, если элемент был успешно добавлен, иначе false
     */
    public boolean add(Person person) {
        return this.set.add(person);
    }

    /**
     * Метод изменения элемента в коллекции по заданному id
     *
     * @param id Id человека, которого нужно изменить
     * @param person Элемент для добавления
     *
     * @return true, если элемент был успешно найден и изменён, иначе false
     */
    public boolean update(long id, Person person) {
        Optional<Person> element = this.set.stream().filter(p -> p.getId() == id).findAny();
        if (element.isEmpty()) return false;
        Person p = element.get();
        p.setName(person.getName());
        p.setCoordinates(person.getCoordinates());
        p.setHeight(person.getHeight());
        p.setPassportID(person.getPassportID());
        p.setEyeColor(person.getEyeColor());
        p.setNationality(person.getNationality());
        p.setLocation(person.getLocation());
        return true;
    }

    /**
     * Метод удаления элемента из коллекции по заданному id
     *
     * @param id Id человека, которого нужно удалить
     *
     * @return true, если элемент был успешно найден и удален, иначе false
     */
    public boolean remove(long id) {
        return this.set.removeIf(p -> p.getId() == id);
    }

    /**
     * Метод очистки коллекции
     */
    public void clear() {
        this.set.clear();
    }

    /**
     * Метод сохранения коллекции в файл
     *
     * @return true, если коллекция была успешно сохранена, иначе false
     */
    public boolean save() {
        if (file == null || !this.canWrite)
            return false;
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file))) {
            writer.write(this.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Метод добавления элемента в коллекцию, если он больше её максимального элемента
     *
     * @param person Элемент для добавления
     *
     * @return true, если элемент был успешно добавлен, иначе false
     */
    public boolean addIfMax(Person person) {
        Person last = this.set.last();
        if (person.compareTo(last) > 0)
            return this.set.add(person);
        return false;
    }

    /**
     * Метод добавления элемента в коллекцию, если он меньше её минимального элемента
     *
     * @param person Элемент для добавления
     *
     * @return true, если элемент был успешно добавлен, иначе false
     */
    public boolean addIfMin(Person person) {
        Person first = this.set.first();
        if (person.compareTo(first) < 0)
            return this.set.add(person);
        return false;
    }

    /**
     * Метод, складывающий рост каждого человека в коллекции
     *
     * @return Суммарный рост всех людей
     */
    public int sumOfHeight() {
        return this.set.stream()
                .mapToInt(Person::getHeight)
                .sum();
    }

    /**
     * Метод, фильтрующий всех людей, в имени которых имеется строка name,
     * проверка не чувствительна к регистру
     *
     * @param name Строка, по которой происходит фильтрация
     *
     * @return Список людей, в имени которых имеется строка name
     */
    public List<Person> filterContainsName(String name) {
        return this.set.stream()
                .filter(p -> p.getName().toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }

    /**
     * Метод, возвращающий отсортированный по убыванию список местоположений всех людей в коллекции
     *
     * @return Отсортированный по убыванию список местоположений
     */
    public List<Location> fieldDescendingLocation() {
        return this.set.stream()
                .map(Person::getLocation)
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "PersonTreeSet(\n" +
                this.set.stream()
                        .map(p -> "\t" + p.toString())
                        .collect(Collectors.joining(",\n")) +
                "\n)";
    }
}
