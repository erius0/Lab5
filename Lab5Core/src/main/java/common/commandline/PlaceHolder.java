package common.commandline;

import java.io.Serializable;

public class PlaceHolder<T> implements Serializable {

    private final Class<T> clazz;

    protected PlaceHolder(Class<T> clazz) {
        this.clazz = clazz;
    }

    public static <T> PlaceHolder<T> of(Class<T> clazz) {
        return new PlaceHolder<>(clazz);
    }

    public Class<T> getExpectedClass() {
        return clazz;
    }

    public static Object[] replacePlaceHoldersWith(Object[] args, Object... replaceWith) {
        Object[] result = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            Object obj = args[i];
            result[i] = obj;
            if (!(obj instanceof PlaceHolder)) continue;
            PlaceHolder<?> placeHolder = (PlaceHolder<?>) obj;
            for (Object replace : replaceWith) {
                if (placeHolder.clazz.isAssignableFrom(replace.getClass())) {
                    result[i] = replace;
                    break;
                }
            }
        }
        return result;
    }
}
