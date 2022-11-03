package api;

public class Utilities {

    public static void throwExceptionIfNull(Object... _o) throws NullPointerException {

        Utilities.ifNullHelper(_o);

        for (var e : _o) {
            Utilities.ifNullHelper(e);
        }
    }

    private static void ifNullHelper(Object _o) throws NullPointerException {
        if (_o == null) throw new NullPointerException();
    }
}
