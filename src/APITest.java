import org.json.JSONObject;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

public class APITest {

    static Pattern ISBN13 = Pattern.compile("\\s*\\d{13}\\s*");
    private static final APIInterface API = new APIAdapter();

    public static void main(String[] args) {
        test("lord of the rings");
        try {
            JSONObject json = new JSONObject(API.getURL(RequestType.ISBN,"9780788789830"));
            json.keySet().forEach(e->{
                switch (e.toLowerCase()) {
                    case "title", "authors", "isbn_13", "genre" -> System.out.println(e + " : " + json.get(e));
                }
            });
            System.out.println(new Book("9780788789830"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void test(String... _args) {
        try{
            JSONObject json = new JSONObject(API.getJsonAsString(RequestType.SEARCH, _args));

            LinkedHashSet<String> set = parseForISBNs(json);
            set.forEach(System.out::println);
            System.out.println("\nNumber of ISBNs: " + set.size());
        }catch (Exception ignored){
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
            } catch (Exception ignored) {
            }
        }
    }
}
