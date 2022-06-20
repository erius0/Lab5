package common.commandline.response;

public enum DefaultResponse implements Response {

    OK("Команда выполнена успешно"),
    FILE_NOT_FOUND("Файл не был найден"),
    CLIENT_ONLY("Команда не предназначена для выполнения на сервере"),
    SERVER_ERROR("Ошибка при связи по сетевому каналу"),
    CLASS_NOT_FOUND("Класс не найден"),
    TYPE_ERROR("Получен ответ не того типа"),
    HOST_NOT_FOUND("Сервер не найден"),
    UNKNOWN("Неизвестная ошибка");

    private final String msg;

    DefaultResponse(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
