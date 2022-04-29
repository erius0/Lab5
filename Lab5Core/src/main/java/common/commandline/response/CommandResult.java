package common.commandline.response;

import java.io.Serializable;

public class CommandResult implements Serializable {

    private final String value;
    private final Response response;

    public CommandResult(String value, Response response) {
        this.value = value;
        this.response = response;
    }

    public String getValue() {
        return value;
    }

    public Response getResponse() {
        return response;
    }
}
