package api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class Book {

    private String key, title;
    private LinkedHashSet<Author> authors = new LinkedHashSet<>();
    private static final APIInterface API = APIInterface.getInstance();
    private static final int NUM_OF_ITERATIONS = 10;
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

    private Book(String _key, Set<Author> _authors, String _title) throws NullPointerException {

        books.add(this);
        Utilities.notNull(_key);

        this.key = _key;
        this.authors = new LinkedHashSet<>(_authors);
        this.title = _title;
    }

    private Book(String _isbn) {

        books.add(this);
        this.key = _isbn;
        JSONObject json;
        String key = null;

        try {

            json = API.getJson(RequestType.ISBN, _isbn);
            key = getKey(json);

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
            if (b.getIsbn().equals(_isbn)) {
                return b;
            }
        }

        return new Book(_isbn);
    }

    private static String processString(String _string) {
        return _string.replace(API.WHITESPACE_AND_PERIODS.pattern(), "").toLowerCase();
    }

    public static LinkedHashSet<Book> getBooksByTitle(String _title) {
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
                        "?q=" + URLEncoder.encode(_title, StandardCharsets.UTF_8));

                if (json.getInt("num_found") > 0) {
                    Iterator<Object> it = json.getJSONArray("docs").iterator();

                    Object temp;
                    Set<String> set;
                    String tempString;
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

    private static Book getBookByKey(String _key) {

        //TODO check books for matching _key
        try {
            JSONObject json;
            if (Book.isValidKey(API.BOOKS_KEY, _key)) {
                json = API.getJson(_key.startsWith("/books/") ? _key : "books/" + _key);
            } else if (Book.isValidKey(API.WORKS_KEY, _key)) {
                json = API.getJson(_key.startsWith("/works/") ? _key : "works/" + _key);
            }
            else throw new IOException("invalid key");

            Set<String> keySet = json.keySet();
            if (keySet.contains("type")) {
                String temp = json.getJSONObject("type").getString("key");
                if (temp.equals("/type/redirect")) {
                    return Book.getBookByKey(json.getString("location"));
                }
            }

            String title;
            if (keySet.contains("title")) {
                title = json.getString("title");
            }
            else throw new IOException("no title");

            LinkedHashSet<String> authorIds = new LinkedHashSet<>();
            LinkedHashSet<Author> authors = new LinkedHashSet<>();
            if (keySet.contains("authors")) {

                json.getJSONArray("authors").forEach(a -> {
                    if (a instanceof JSONObject && ((JSONObject) a).keySet().contains("author"))
                        authorIds.add(((JSONObject) a).getJSONObject("author").getString("key"));
                });

                authorIds.forEach(id -> authors.add(Author.getAuthorById(id)));
            }

            return new Book(_key, authors, title);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Book.NULL_BOOK;
    }

    //TODO
    private static String booksKeyToWorksKey(String _booksKey) {
        try {
            JSONObject json = API.getJson(_booksKey);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return _booksKey;
    }

    private static boolean isValidKey(Pattern _pattern, String _key) {
        return _pattern.matcher(_key).matches();
    }

    public HashMap<String, Object> toMap() {
        return this.toMap(new HashMap<>());
    }

    public <T extends Map<String, Object>> T toMap(T _map) {
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

    public JSONObject toJsonObject() {
        return new JSONObject(this.toMap());
    }

    public String getIsbn() {
        return this.key;
    }

    public LinkedHashSet<Author> getAuthors() {
        return this.authors;
    }

    public String getAuthorNamesFlattened() {
        return Utilities.flatten(getAuthorNames());
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
        return String.format("%s ISBN: %s, Author%s: %s, Title: %s", super.toString(), this.getIsbn(),
                (this.getAuthors().size() > 1 ? "s" : ""),
                this.getAuthorNamesFlattened(), this.getTitle());
    }
}