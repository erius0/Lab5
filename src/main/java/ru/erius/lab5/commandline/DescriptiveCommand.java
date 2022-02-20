package ru.erius.lab5.commandline;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * Класс команд с описанием, оболочка для обычных команд, используются для корректной работы команды help
 */
@Data @AllArgsConstructor
public class DescriptiveCommand {
    @NonNull
    private final Command command;
    private final String desc;
}
