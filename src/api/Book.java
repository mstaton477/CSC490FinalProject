package api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class Book {

    private String key, title, isbn;
    private LinkedHashSet<Author> authors = new LinkedHashSet<>();
    private static final APIInterface API = APIInterface.getInstance();
    private static final String LIMIT = "5";
    private static final LinkedHashSet<Book> books = new LinkedHashSet<>();
    private static final Book NULL_BOOK = new Book() {
        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public String getIsbn() {
            return null;
        }

        @Override
        public JSONObject toJsonObject() {
            return new JSONObject();
        }

        @Override
        public String toString() {
            return null;
        }
    };

    private Book() {
    }

    private Book(String _key, Set<Author> _authors, String _title, String _isbn) throws NullPointerException {

        Utilities.throwExceptionIfNull(_key);
        books.add(this);

        this.key = _key;
        this.authors = new LinkedHashSet<>(_authors);
        this.title = _title;
        this.isbn = _isbn;
    }

    private static String getKeyFromIsbn(String _isbn) throws IOException {
        return getKey(API.getJson("isbn/" + _isbn));
    }

    /*


    private Book(String _isbn) {

        this.isbn = _isbn;
        books.add(this);
        JSONObject json;

        try {

            json = API.getJson("isbn/" + _isbn);
            this.key = getKey(json);

            this.title = json.getString("title");
            this.authors = setAndGetAuthors(json);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {

                if (this.authors == null || this.authors.isEmpty()) {
                    for (int i = NUM_OF_ITERATIONS; key != null && i > 0; i--) {

                        json = API.getJson(key, "");
                        if (json.keySet().contains("authors")) {
                            this.authors = setAndGetAuthors(json);
                            break;
                        } else {
                            key = getKey(json);
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
     */

    private static String getKey(JSONObject json) {

        Set<String> keySet = json.keySet();
        if (keySet.contains("works"))
            return json.getJSONArray("works").getJSONObject(0).getString("key");
        else if (keySet.contains("key"))
            return json.getString("key");

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

    public static Book getBookByIsbn(String _isbn) throws IOException {

        for (Book b : books) {
            if (b.getIsbn().equals(_isbn)) {
                return b;
            }
        }

        return Book.getBookByKey(Book.getKeyFromIsbn(_isbn), _isbn);
    }

    private static String processString(String _string) {
        return _string.replace(API.WHITESPACE_AND_PERIODS.pattern(), "").toLowerCase();
    }

    /**
     * @param _title The title (or other keywords) to be searched
     * @param _limit The number of books desired to be returned; if {@code _limit.isEmpty()}, uses {@code Book.LIMIT} instead
     * @return A JSONArray of {@code _limit} many JSONObjects corresponding to the books returned by the search
     */
    public static JSONArray getBooksByTitleAsJSONArray(String _title, String _limit) {
        Set<Book> bookSet = Book.getBooksByTitle(_title, _limit);
        Set<JSONObject> jsonSet = new HashSet<>();
        bookSet.forEach(e -> jsonSet.add(e.toJsonObject()));
        return new JSONArray(jsonSet);
    }

    public static LinkedHashSet<Book> getBooksByTitle(String _title){
        return Book.getBooksByTitle(_title, "");
    }

    public static LinkedHashSet<Book> getBooksByTitle(String _title, String _limit) {
        LinkedHashSet<Book> list = new LinkedHashSet<>();
        String tempTitle = Book.processString(_title);

        do {
            for (Book b : books) {
                if (Book.processString(b.getTitle()).equals(tempTitle)) {
                    list.add(b);
                    break;
                }
            }

            try {
                JSONObject json = API.getJson("search",
                        "?q=" + URLEncoder.encode(_title, StandardCharsets.UTF_8)
                                + "&limit=" + (_limit.isEmpty() ? Book.LIMIT : _limit));

                if (json.getInt("num_found") > 0) {
                    Iterator<Object> it = json.getJSONArray("docs").iterator();

                    Object temp;
                    Set<String> set;
                    while (it.hasNext()) {
                        if ((temp = it.next()) instanceof JSONObject) {
                            set = (json = (JSONObject) temp).keySet();
                            if (set.contains("key")) {
                                list.add(Book.getBookByKey(json.getString("key")));
                            }
                        }
                    }
                }
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            list.add(Book.NULL_BOOK);
        } while (false);

        if (list.size() > 1) list.remove(Book.NULL_BOOK);
        return list;
    }

    public static Book getBookByKey(String _key) {
        return Book.getBookByKey(_key, "");
    }

    public static Book getBookByKey(String _key, String _isbn) {

        //TODO check books for matching _key
        try {
            JSONObject json;
            if (Book.isValidKey(API.BOOKS_KEY, _key)) {
                json = API.getJson(_key.startsWith("/books/") ? _key : "books/" + _key);
            } else if (Book.isValidKey(API.WORKS_KEY, _key)) {
                json = API.getJson(_key.startsWith("/works/") ? _key : "works/" + _key);
            } else throw new IOException("invalid key: " + _key);

            Set<String> keySet = json.keySet();
            if (keySet.contains("type")) {
                String temp = json.getJSONObject("type").getString("key");
                if (temp.equals("/type/redirect")) {
                    return Book.getBookByKey(json.getString("location"));
                }
            }

            String title = "";
            if (keySet.contains("title")) {
                title = json.getString("title");
            }

            if (keySet.contains("isbn")) {
                _isbn = json.getString("isbn");
            }

            LinkedHashSet<String> authorIds = new LinkedHashSet<>();
            LinkedHashSet<Author> authors = new LinkedHashSet<>();
            if (keySet.contains("authors")) {

                json.getJSONArray("authors").forEach(a -> {
                    if (a instanceof JSONObject && ((JSONObject) a).keySet().contains("author"))
                        authorIds.add(((JSONObject) a).getJSONObject("author").getString("key"));
                });

                authorIds.forEach(id -> authors.add(Author.getAuthorById(id)));
            }

            return new Book(_key, authors, title, _isbn);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Book.NULL_BOOK;
    }

    private static boolean isValidKey(Pattern _pattern, String _key) {
        return _pattern.matcher(_key).find();
    }

    public HashMap<String, Object> toMap() {
        return this.toMap(new HashMap<>());
    }

    public <T extends Map<String, Object>> T toMap(T _map) {
        _map.put("key", this.getKey());
        _map.put("isbn", this.getIsbn());

        if (this.getTitle() != null)
            _map.put("title", this.getTitle());

        if (this.getAuthors().size() > 0) {
            JSONArray tempArr = new JSONArray();
            this.getAuthors().forEach(e -> tempArr.put(e.toJsonObject()));
            _map.put("authors", tempArr);
        }

        return _map;
    }

    public String getKey() {
        return this.key;
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

    public LinkedList<String> getAuthorNames() {
        LinkedList<String> authorNames = new LinkedList<>();
        for (Author a : this.getAuthors()) {
            authorNames.add(a.getName());
        }
        return authorNames;
    }

    public LinkedList<String> getAuthorIds() {
        LinkedList<String> authorIds = new LinkedList<>();
        for (Author a : this.getAuthors()) {
            authorIds.add(a.getId());
        }
        return authorIds;
    }

    public static String getAuthorName(String _authorID) {
        try {
            return API.getJson(RequestType.AUTHORS, _authorID).getString("name");
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public String getTitle() {
        return this.title;
    }

    void setIsbn(String _isbn) {
        this.key = _isbn;
    }

    void setAuthors(LinkedHashSet<Author> _authors) {
        this.authors = _authors;
    }

    void setTitle(String _title) {
        this.title = _title;
    }

    @Override
    public String toString() {
        return super.toString() + "; " + this.toJsonObject().toString();
    }
}