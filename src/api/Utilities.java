package api;

public class Utilities {

    public static void notNull(Object... _o) throws NullPointerException {

        throwExceptionIfNull(_o);

        for (var e : _o) {
            throwExceptionIfNull(e);
        }
    }

    public static void throwExceptionIfNull(Object _o) throws NullPointerException {
        if (_o == null) throw new NullPointerException();
    }
}
