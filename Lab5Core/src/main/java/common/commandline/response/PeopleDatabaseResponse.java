package common.commandline.response;

public enum PeopleDatabaseResponse implements Response {

    ELEMENT_NOT_FOUND("Элемент не был найден"),
    SAVE_FAILED("Не удалось сохранить коллекцию"),
    FAILED_TO_ADD("Не удалось добавить элемент в коллекцию");

    private final String msg;

    PeopleDatabaseResponse(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
