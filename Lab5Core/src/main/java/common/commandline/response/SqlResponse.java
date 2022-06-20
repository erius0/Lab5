package common.commandline.response;

public enum SqlResponse implements Response {

    OK("Запрос успешно выполнен"),
    LOGIN_EXISTS("Не удалось зарегистрироваться, пользователь существует"),
    WRONG_CREDENTIALS("Логин или пароль неверны, повторите попытку"),
    NO_CHANGES("Никаких изменений не было внесено"),
    NO_PERMISSION("Недостаточно прав для изменения элемента"),
    NOT_FOUND("Элемент не найден"),
    UNKNOWN("Непредвиденная ошибка при работе с базой данных");

    private final String msg;

    SqlResponse(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
