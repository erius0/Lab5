package ru.erius.lab5;

import ru.erius.lab5.cli.CommandParser;
import ru.erius.lab5.collection.PersonTreeSet;

public class Lab5 {
    /**
     * Создание коллекции {@link PersonTreeSet PersonTreeSet}
     * и парсера {@link CommandParser CommandParser}, запуск программы на выполнение
     *
     * @param args Аргументы командной строки
     */
    public static void main(String[] args) {
        PersonTreeSet pts = new PersonTreeSet();
        CommandParser cmd = new CommandParser(pts);
        cmd.start();
    }
}
