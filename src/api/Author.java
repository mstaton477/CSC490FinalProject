package api;
import org.json.JSONObject;

import java.util.LinkedHashSet;

public class Author {
    private static final APIInterface API = APIInterface.getInstance();
    private String id, name;

    private static final LinkedHashSet<Author> authors = new LinkedHashSet<>();

    private Author() {
        authors.add(this);
    }

    private Author(String _id) {
        this();
        this.id = _id;
        try {
            this.name = new JSONObject(API.getJsonAsString(RequestType.AUTHORS, _id)).getString("name");
        } catch (Exception ignored) {
            this.name = "";
        }
    }

    public static Author getAuthor(String _id) {
        for (Author a : authors) {
            if (a.id.equals(_id)) {
                return a;
            }
        }
        return new Author(_id);
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }
}
