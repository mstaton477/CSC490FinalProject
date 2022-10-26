package api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class Book {
    private String isbn, title;
    private LinkedList<Author> authors;
    private static final APIInterface API = APIInterface.getInstance();
    private static final int AUTHOR_SUBSTRING_STARTING_INDEX = 9;
    private static final LinkedHashSet<Book> books = new LinkedHashSet<>();

    private Book() {
        books.add(this);
    }

    private Book(String _isbn, LinkedList<Author> _authors, String _title) throws NullPointerException {
        this();
        Utilities.notNull(_isbn);

        this.isbn = _isbn;
        this.authors = _authors;
        this.title = _title;
    }

    private Book(String _isbn) {
        this();
        this.isbn = _isbn;
        try {
            JSONObject json = new JSONObject(API.getJsonAsString(RequestType.ISBN, _isbn));

            this.title = json.getString("title");

            this.authors = new LinkedList<>();
            JSONArray authorsArray = json.getJSONArray("authors");
            Iterator<Object> authorsIterator = authorsArray.iterator();
            JSONObject temp;
            while (authorsIterator.hasNext()) {
                temp = (JSONObject) authorsIterator.next();
                this.authors.add(
                        Author.getAuthor(temp.getString("key").substring(AUTHOR_SUBSTRING_STARTING_INDEX))
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            this.authors = new LinkedList<>();
            this.title = null;
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

    public String getIsbn() {
        return this.isbn;
    }

    public LinkedList<Author> getAuthors() {
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

    void setAuthors(LinkedList<Author> _authors) {
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
