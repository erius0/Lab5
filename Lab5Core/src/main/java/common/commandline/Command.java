package common.commandline;

import common.commandline.response.CommandResult;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
public abstract class Command implements Serializable {

    protected final String alias;
    protected final String description;
    protected final boolean clientOnly;
    @Setter
    protected Executable executable;

    protected Object[] args;

    public Command(String alias, boolean clientOnly, String description) {
        this.alias = alias;
        this.clientOnly = clientOnly;
        this.description = description;
    }

    public Command(String alias, boolean clientOnly, String description, Executable executable) {
        this(alias, clientOnly, description);
        this.executable = executable;
    }

    public abstract boolean validate(String[] args);

    public CommandResult executeOnClient() {
        return executable.execute(args);
    }
}
