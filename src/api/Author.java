package api;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Author {

    private static final APIInterface API = APIInterface.getInstance();
    private String id, name;

    private static final LinkedHashSet<Author> authors = new LinkedHashSet<>();
    public static final Author NULL_AUTHOR = new Author() {
        @Override
        public String getId() {
            return "";
        }

        @Override
        public String getName() {
            return "";
        }
    };

    private Author() {
    }

    private Author(String _id) {

        authors.add(this);
        this.id = _id;

        try {
            this.name = API.getJson(_id, "").getString("name");
        } catch (Exception ex) {
            ex.printStackTrace();
            this.name = "";
        }
    }

    public static @NotNull Author getAuthorById(@NotNull String _key) {

        if (!_key.startsWith("/authors/")) {
            _key = "/authors/" + _key;
        }

        for (Author a : authors) {
            if (a.getId().equals(_key)) {
                return a;
            }
        }

        return new Author(_key);
    }

    public static @NotNull LinkedList<Author> getAuthorsByName(String _name) {
        return Author.getAuthorsByName(_name, "");
    }

    public static @NotNull LinkedList<Author> getAuthorsByName(String _name, String _limit) {

        Utilities.throwExceptionIfNull(_name);

        LinkedList<Author> tempAuthorList = new LinkedList<>();
        for (Author a : authors) {
            if (a.getName().equals(_name)) {
                tempAuthorList.add(a);
            }
        }

        if (tempAuthorList.size() == 0) try {
            API.getJson("search/authors", "?q=" + URLEncoder.encode(_name, StandardCharsets.UTF_8) +
                            (_limit.isEmpty() ? "" : "&limit=" + _limit)).getJSONArray("docs")
                    .forEach(e -> {
                        if (e instanceof JSONObject) {
                            tempAuthorList.add(Author.getAuthorById(((JSONObject) e).getString("key")));
                        }
                    });

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return tempAuthorList;
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public JSONObject toJsonObject() {

        HashMap<String, String> map = new HashMap<>();
        map.put("name", this.getName());
        map.put("id", this.getId());

        return new JSONObject(map);
    }

    @Override
    public String toString() {
        return String.format("%s\tname: %s\tid: %s", super.toString(), this.getName(), this.getId());
    }
}
