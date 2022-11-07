package api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

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

    public static String format(String _type, JSONArray _arr){

        Map<String, JSONArray> map = new HashMap<>();
        map.put(_type, _arr);
        return new JSONObject(map).toString();
    }

    public static String format(String _type, JSONObject... _jsons){
        return Utilities.format(_type, new JSONArray(_jsons));
    }

    public static String format(String _type, Collection<JSONObject> _jsons){
        return Utilities.format(_type, new JSONArray(_jsons));
    }

    public static  LinkedList<JSONObject> toJsonList(List<Author> _list){

        LinkedList<JSONObject> jsons = new LinkedList<>();

        _list.forEach(e -> jsons.add(e.toJsonObject()));
        return jsons;
    }
}
