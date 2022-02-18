package ru.erius.lab5.commandline;

@FunctionalInterface
public interface Command {
    void execute(String[] args);
}
