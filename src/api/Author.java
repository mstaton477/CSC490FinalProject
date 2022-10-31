package api;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

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
            this.name = API.getJsonAsString(_id, "").getString("name");
        } catch (Exception ignored) {
            this.name = "";
        }
    }

    public static Author getAuthorById(String _id) {
        if(!_id.startsWith("/authors/")){
            _id = "/authors/" + _id;
        }
        for (Author a : authors) {
            if (a.id.equals(_id)) {
                return a;
            }
        }
        return new Author(_id);
    }

    public static LinkedList<Author> getAuthorsByName(String _name) {
        if (_name == null) return null;

        LinkedList<Author> tempAuthorList = new LinkedList<>();
        for (Author a : authors) {
            if (a.name.equals(_name)) {
                tempAuthorList.add(a);
            }
        }

        if (tempAuthorList.size() == 0) try {
            API.getJsonSearchAsString(RequestType.AUTHORS, _name).getJSONArray("docs")
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
        map.put("name", this.name);
        map.put("id", this.id);
        return new JSONObject(map);
    }

    @Override
    public String toString() {
        return String.format("%s\tname: %s\tid: %s", super.toString(), this.name, this.id);
    }
}
