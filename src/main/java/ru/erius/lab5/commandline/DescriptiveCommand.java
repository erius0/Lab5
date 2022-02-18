package ru.erius.lab5.commandline;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data @AllArgsConstructor
public class DescriptiveCommand {
    @NonNull
    private final Command command;
    private final String desc;
}
