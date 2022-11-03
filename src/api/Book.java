package api;

import org.jetbrains.annotations.*;
import org.json.*;

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
        @Contract(pure = true)
        @Override
        public @Nullable String getTitle() {
            return null;
        }

        @Contract(pure = true)
        @Override
        public @Nullable String getIsbn() {
            return null;
        }

        @Contract(value = " -> new", pure = true)
        @Override
        public @NotNull JSONObject toJsonObject() {
            return new JSONObject();
        }

        @Contract(pure = true)
        @Override
        public @Nullable String toString() {
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

    private static @Nullable String getKey(@NotNull JSONObject json) {

        Set<String> keySet = json.keySet();
        if (keySet.contains("works")) return json.getJSONArray("works").getJSONObject(0).getString("key");

        else if (keySet.contains("key")) return json.getString("key");

        else return null;
    }

    private static @NotNull LinkedHashSet<Author> setAndGetAuthors(JSONObject json) {

        LinkedHashSet<Author> set = new LinkedHashSet<>();
        setAuthors(set, json);
        return set;
    }

    private static void setAuthors(Collection<Author> c, @NotNull JSONObject json) {

        JSONArray authorsArray = json.getJSONArray("authors");
        Iterator<Object> authorsIterator = authorsArray.iterator();
        JSONObject temp;

        while (authorsIterator.hasNext())
            if ((temp = (JSONObject) authorsIterator.next()).keySet().contains("author")) {
                temp = temp.getJSONObject("author");
                c.add(Author.getAuthorById(temp.getString("key")));
            }

    }

    public static Book getBookByIsbn(String _isbn) throws IOException {

        for (Book b : books) if (b.getIsbn().equals(_isbn)) return b;

        return Book.getBookByKey(Book.getKeyFromIsbn(_isbn), _isbn);
    }

    private static @NotNull String processString(@NotNull String _string) {
        return _string.replace(API.WHITESPACE_AND_PERIODS.pattern(), "").toLowerCase();
    }

    /**
     * @param _title The title (or other keywords) to be searched
     * @param _limit The number of books desired to be returned; if {@code _limit.isEmpty()}, uses {@code Book.LIMIT} instead
     * @return A JSONArray of at most {@code _limit} many JSONObjects corresponding to the books returned by the search
     */
    @Contract("_, _ -> new")
    public static @NotNull JSONArray getBooksByTitleAsJSONArray(String _title, String _limit) {

        Set<Book> bookSet = Book.getBooksByTitle(_title, _limit);
        Set<JSONObject> jsonSet = new HashSet<>();
        bookSet.forEach(e -> jsonSet.add(e.toJsonObject()));

        return new JSONArray(jsonSet);
    }

    public static @NotNull LinkedHashSet<Book> getBooksByTitle(String _title) {
        return Book.getBooksByTitle(_title, "");
    }

    public static @NotNull LinkedHashSet<Book> getBooksByTitle(String _title, String _limit) {

        LinkedHashSet<Book> linkedHashSet = new LinkedHashSet<>();
        String tempTitle = Book.processString(_title);

        for (Book b : books) if (Book.processString(b.getTitle()).equals(tempTitle)) linkedHashSet.add(b);

        if (linkedHashSet.isEmpty()) try {

            JSONObject json = API.getJson("search",
                    "?q=" + URLEncoder.encode(_title, StandardCharsets.UTF_8)
                            + "&limit=" + (_limit.isEmpty() ? Book.LIMIT : _limit));

            if (json.getInt("num_found") > 0) {
                Iterator<Object> it = json.getJSONArray("docs").iterator();
                Object temp;
                while (it.hasNext()) if ((temp = it.next()) instanceof JSONObject)
                    if ((json = (JSONObject) temp).keySet().contains("key"))
                        linkedHashSet.add(Book.getBookByKey(json.getString("key")));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // include the NULL_BOOK only if it is the unique book in the linkedHashSet
        if (linkedHashSet.size() < 1) linkedHashSet.add(Book.NULL_BOOK);
        return linkedHashSet;
    }

    public static Book getBookByKey(String _key) {
        return Book.getBookByKey(_key, "");
    }

    public static Book getBookByKey(String _key, String _isbn) {

        try {
            JSONObject json;
            if (Book.isValidKey(API.BOOKS_KEY, _key))
                json = API.getJson(_key.startsWith("/books/") ? _key : "books/" + _key);
            else if (Book.isValidKey(API.WORKS_KEY, _key))
                json = API.getJson(_key.startsWith("/works/") ? _key : "works/" + _key);
            else throw new IOException("invalid key: " + _key);

            Set<String> keySet = json.keySet();
            if (keySet.contains("type")) {
                String temp = json.getJSONObject("type").getString("key");
                if (temp.equals("/type/redirect")) return Book.getBookByKey(json.getString("location"));
            }

            String title = "";
            if (keySet.contains("title")) title = json.getString("title");


            if (keySet.contains("isbn")) _isbn = json.getString("isbn");


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

    private static boolean isValidKey(@NotNull Pattern _pattern, String _key) {
        return _pattern.matcher(_key).find();
    }

    public HashMap<String, Object> toMap() {
        return this.toMap(new HashMap<>());
    }

    public <T extends Map<String, Object>> T toMap(@NotNull T _map) {
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