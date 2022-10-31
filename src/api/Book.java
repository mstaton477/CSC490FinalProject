package api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.regex.Pattern;

public class Book {

    static Pattern ISBN13 = Pattern.compile("\\s*\\d{13}\\s*");
    private String isbn, title;
    private LinkedHashSet<Author> authors = new LinkedHashSet<>();
    private static final APIInterface API = APIInterface.getInstance();
    private static final int NUM_OF_ITERATIONS = 10;
    private static final LinkedHashSet<Book> books = new LinkedHashSet<>();

    private Book() {
        books.add(this);
    }

    private Book(String _isbn, Set<Author> _authors, String _title) throws NullPointerException {
        this();
        Utilities.notNull(_isbn);

        this.isbn = _isbn;
        this.authors = new LinkedHashSet<>(_authors);
        this.title = _title;
    }

    private Book(String _isbn) {
        this();
        this.isbn = _isbn;
        JSONObject json;
        String key = null;
        try {
            json = new JSONObject(API.getJsonAsString(RequestType.ISBN, _isbn));

            key = getKey(json);

            this.title = json.getString("title");

            this.authors = setAndGetAuthors(json);

        } catch (Exception ignored) {
        } finally {
            try {
                if (this.authors == null || this.authors.isEmpty()) {
                    for (int i = NUM_OF_ITERATIONS; key != null && i > 0; i--) {
                        json = new JSONObject(API.getJsonAsString(key, ""));
                        if (json.keySet().contains("authors")) {
                            this.authors = setAndGetAuthors(json);
                            break;
                        } else {
                            key = getKey(json);
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    private String getKey(JSONObject json) {
        Set<String> keySet = json.keySet();
        if (keySet.contains("works"))
            return json.getJSONArray("works").getJSONObject(0).getString("key");
        else return null;
    }

    private static LinkedHashSet<Author> setAndGetAuthors(JSONObject json) {
        LinkedHashSet<Author> set = new LinkedHashSet<>();
        setAuthors(set, json);
        return set;
    }

    private static void setAuthors(Collection<Author> c, JSONObject json) {
        JSONArray authorsArray = json.getJSONArray("authors");
        Iterator<Object> authorsIterator = authorsArray.iterator();
        JSONObject temp;
        while (authorsIterator.hasNext()) {
            temp = (JSONObject) authorsIterator.next();
            if (temp.keySet().contains("author")) temp = temp.getJSONObject("author");
            c.add(Author.getAuthorById(temp.getString("key")));
        }
    }

    public static Book getBook(String _isbn) {
        for (Book b : books) {
            if (b.isbn.equals(_isbn)) {
                return b;
            }
        }
        return new Book(_isbn);
    }

    public HashMap<String, Object> toMap() {
        return toMap(new HashMap<>());
    }

    public <T extends Map<String, Object>> T toMap(T _map) {
        _map.put("isbn", this.isbn);
        if (this.title != null)
            _map.put("title", this.title);
        if (this.authors.size() > 0) {
            JSONArray tempArr = new JSONArray();
            this.authors.forEach(e -> {
                tempArr.put(e.toJsonObject());
            });
            _map.put("authors", tempArr);
        }


        return _map;
    }

    public JSONObject toJsonObject() {
        return new JSONObject(this.toMap());
    }

    public String getIsbn() {
        return this.isbn;
    }

    public LinkedHashSet<Author> getAuthors() {
        return this.authors;
    }

    public String getAuthorNamesFlattened() {
        return Utilities.flatten(getAuthorNames());
    }

    public LinkedList<String> getAuthorNames() {
        LinkedList<String> authorNames = new LinkedList<>();
        for (Author a : this.authors) {
            authorNames.add(a.getName());
        }
        return authorNames;
    }

    public LinkedList<String> getAuthorIds() {
        LinkedList<String> authorIds = new LinkedList<>();
        for (Author a : this.authors) {
            authorIds.add(a.getId());
        }
        return authorIds;
    }

    public static String getAuthorName(String _authorID) {
        try {
            return new JSONObject(API.getJsonAsString(RequestType.AUTHORS, _authorID)).getString("name");
        } catch (Exception ignored) {
            return "";
        }
    }

    public String getTitle() {
        return this.title;
    }

    void setIsbn(String _isbn) {
        this.isbn = _isbn;
    }

    void setAuthors(LinkedHashSet<Author> _authors) {
        this.authors = _authors;
    }

    void setTitle(String _title) {
        this.title = _title;
    }

    @Override
    public String toString() {
        return String.format("%s ISBN: %s, Author%s: %s, Title: %s", super.toString(), this.isbn, (this.authors.size() > 1 ? "s" : ""),
                this.getAuthorNamesFlattened(), this.title);
    }
}
