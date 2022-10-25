import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class APITest {

    static Pattern ISBN13 = Pattern.compile("\\s*\\d{13}\\s*");
    private static final APIInterface API = APIInterface.getInstance();

    public static void main(String[] args) {
        test("lord of the rings");
        try {
            JSONObject json = new JSONObject(API.getUrl(RequestType.ISBN, "9780788789830"));
            json.keySet().forEach(e -> {
                switch (e.toLowerCase()) {
                    case "title", "authors", "isbn_13", "genre" -> System.out.println(e + " : " + json.get(e));
                }
            });
            System.out.println(Book.getBook("9780788789830"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void test(String _arg) {
        try {
            JSONObject json = new JSONObject(API.getJsonSearchAsString(null, _arg));

            LinkedHashSet<String> set = parseForISBNs(json);
            System.out.println("Number of ISBNs: " + set.size());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //TODO make more robust: replace String literals with enumerations
    private static LinkedHashSet<String> parseForISBNs(JSONObject _json) {
        LinkedHashSet<String> isbns = new LinkedHashSet<>();

        for (var field : _json.getJSONArray("docs")) {
            if (field instanceof JSONObject)
                parseForISBNHelper((JSONObject) field, isbns);
            else System.out.println("Not JSONObject; is actually " + field.getClass());
        }

        return isbns;
    }

    private static void parseForISBNHelper(JSONObject _e, LinkedHashSet<String> _isbns) {
        if (_e.keySet().contains("isbn")) {
            try {
                _e.getJSONArray("isbn").forEach(e -> {
                    if (e instanceof String && ISBN13.matcher((String) e).matches())
                        _isbns.add(((String) e).trim());
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
