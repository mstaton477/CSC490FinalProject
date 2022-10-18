import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class Book {
    private String isbn, title, genre;

    //TODO change to a Map taking authorIDs to authorNames
    private LinkedList<String> authors;
    private static final APIInterface API = APIInterface.getInstance();
    private static final int AUTHOR_SUBSTRING_STARTING_INDEX = 9;
    private static final LinkedHashSet<Book> books = new LinkedHashSet<>();

    private Book() {
        books.add(this);
    }

    public Book(@NotNull String _isbn, LinkedList<String> _authors, String _title, String _genre) throws NullPointerException {
        this();
        Utilities.notNull(_isbn);

        this.isbn = _isbn;
        this.authors = _authors;
        this.title = _title;
        this.genre = _genre;
    }

    public Book(String _isbn, LinkedList<String> _authors, String _title) {
        this(_isbn, _authors, _title, null);
    }

    public Book(String _isbn) {
        this();
        this.isbn = _isbn;
        try {
            JSONObject json = new JSONObject(API.getJsonAsString(RequestType.ISBN, _isbn));

            try {
                this.title = json.getString("title");
            } catch (Exception ignored) {
                this.title = null;
            }

            try {
                this.authors = new LinkedList<>();
                JSONArray authorsArray = json.getJSONArray("authors");
                Iterator<Object> authorsIterator = authorsArray.iterator();
                JSONObject temp;
                while (authorsIterator.hasNext()) {
                    temp = (JSONObject) authorsIterator.next();
                    this.authors.add(temp.getString("key").substring(AUTHOR_SUBSTRING_STARTING_INDEX));
                }
            } catch (Exception ex) {
                this.authors = new LinkedList<>();
            }

            try {
                this.genre = json.getString("subject");
            } catch (Exception ignored) {
                this.genre = null;
            }
        } catch (Exception ex) {
            this.authors = new LinkedList<>();
            this.title = null;
            this.genre = null;
        }
    }

    /**
     * TODO test if this works correctly
     */
    public static @NotNull Book getBook(String _isbn) {
        for (Book b : books) {
            if (b.isbn.equals(_isbn)) {
                return b;
            }
        }
        return new Book(_isbn);
    }

    public String getIsbn() {
        return this.isbn;
    }

    public LinkedList<String> getAuthors() {
        return this.authors;
    }

    public String getAuthorNamesFlattened(){
        return Utilities.flatten(getAuthorNames());
    }

    public LinkedList<String> getAuthorNames() {
        return this.authors.stream().map(Book::getAuthorName).collect(Collectors.toCollection(LinkedList::new));
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

    public String getGenre() {
        return this.genre;
    }

    void setIsbn(String _isbn) {
        this.isbn = _isbn;
    }

    void setAuthors(LinkedList<String> _authors) {
        this.authors = _authors;
    }

    void setTitle(String _title) {
        this.title = _title;
    }

    void setGenre(String _genre) {
        this.genre = _genre;
    }

    @Override
    public String toString() {
        return String.format("ISBN: %s, Author%s: %s, Title: %s%s", isbn, (this.authors.size() > 1 ? "s" : ""), Utilities.flatten(getAuthorNames()), title, (genre == null ? "" : genre));
    }


}
