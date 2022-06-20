package common.collection;

import lombok.Getter;
import common.data.Person;

import java.time.LocalDate;
import java.util.Collections;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class PeopleCollection {

    private final static String TYPE = "TreeSet";

    @Getter
    private final NavigableSet<Person> collection = Collections.synchronizedNavigableSet(new TreeSet<>());
    @Getter
    private final LocalDate initDate = LocalDate.now();

    public String info() {
        return String.format("Тип коллекции: %s \n" +
                        "Дата инициализации: %s \n" +
                        "Количество элементов: %d \n",
                TYPE, this.initDate, this.collection.size());
    }

    public void initFromDatabase(String url, String login, String password) {

    }

    @Override
    public String toString() {
        String result = "PeopleDatabase(";
        result += this.collection.stream().map(Person::toString).collect(Collectors.joining(", "));
        result += ")";
        return result;
    }
}
