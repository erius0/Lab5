package common.commandline;

import common.commandline.response.CommandResult;
import lombok.Getter;

import java.io.Serializable;

@Getter
public abstract class Command implements Serializable {

    protected final String alias;
    protected final String description;
    protected final boolean clientOnly;

    public Command(String alias, boolean clientOnly, String description) {
        this.alias = alias;
        this.clientOnly = clientOnly;
        this.description = description;
    }

    public Object[] validate(String[] args) {
        return new Object[]{};
    }

    public abstract CommandResult execute(Object[] args);
}
