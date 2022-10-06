import org.json.*;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class APITest {

    static Pattern ISBN13 = Pattern.compile("\\s*\\d{13}\\s*");

    public static void main(String[] args) throws IOException {
        test("lord of the rings");
    }

    private static void test(String... _args) throws IOException {
        String s = "http://www.openlibrary.org/search" + getIndexSafe(_args, 1) + ".json?q="
                + URLEncoder.encode(getIndexSafe(_args, 0), StandardCharsets.UTF_8);

        JSONObject json = new JSONObject(new String(new URL(s).openStream().readAllBytes(), StandardCharsets.UTF_8));

        Set<String> set = parseForISBNs(json);
        set.forEach(System.out::println);
        System.out.println("\nNumber of ISBNs: " + set.size());
    }


    private static Set<String> parseForISBNs(JSONObject _json) {
        Set<String> isbns = new HashSet<>();

        for (var field : _json.getJSONArray("docs")) {
            if (field instanceof JSONObject) {
                parseForISBNHelper((JSONObject) field, isbns);
            } else System.out.println("Not JSONObject; is actually " + field.getClass());
        }

        return isbns;
    }

    private static void parseForISBNHelper(JSONObject _e, Set<String> _isbns) {
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

    private static String getIndexSafe(String[] _args, int _index) {
        return _args.length > _index ? _args[_index] : "";
    }
}
