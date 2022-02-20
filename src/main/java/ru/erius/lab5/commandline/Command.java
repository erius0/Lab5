package ru.erius.lab5.commandline;

/**
 * Функциональный интерфейс с одним методом {@link #execute(String[])},
 * используется для реализации шаблон проектирования Command
 */
@FunctionalInterface
public interface Command {
    void execute(String[] args);
}
