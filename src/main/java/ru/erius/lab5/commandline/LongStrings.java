package ru.erius.lab5.commandline;

public enum LongStrings {
    LINE("=================================================================================================="),

    GREETINGS(LINE.value + "\n" +
            "Добро пожаловать в программу для управления коллекцией объектов в интерактивном режиме!\n" +
            "Напишите help, чтобы увидеть доступные команды\n" +
            "Напишите exit, чтобы выйти из программы\n" +
            LINE.value + "\n");

    private final String value;

    LongStrings(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
