package common.commandline;

import common.commandline.response.CommandResult;

import java.io.Serializable;

@FunctionalInterface
public interface Executable extends Serializable {
    CommandResult execute(Object[] args);
}
